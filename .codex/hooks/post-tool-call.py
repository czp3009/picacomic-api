import argparse
import hashlib
import json
import os
import re
import sys
import tempfile
import traceback
from contextlib import closing
from datetime import datetime, timezone
from http.client import HTTPConnection, HTTPException
from pathlib import Path
from typing import Optional

WEBSERVER_HOST = "localhost"
WEBSERVER_ENDPOINT = "/api/provenance/call"
PORT_FILE_SUFFIX = "-provenance-port.txt"


class ProvenanceHookError(RuntimeError):
    pass


def http_request(method, host, port, location, *, body: Optional[bytes] = None, headers={}, timeout=None,
                 wait_for_response=False) -> bytes:
    with closing(HTTPConnection(host, port, timeout=timeout)) as connection:
        connection.request(method, location, body=body, headers=headers)
        if wait_for_response:
            response = connection.getresponse()
            responseText = response.read()


def find_project_root(data):
    claude_root = os.getenv("CLAUDE_PROJECT_DIR")
    if claude_root:
        return str(Path(claude_root).resolve())

    cwd = Path(data.get("cwd") or os.getcwd()).resolve()
    for path in (cwd, *cwd.parents):
        if (path / ".git").exists():
            return str(path)
    return str(cwd)


def get_server_port(project_root):
    path_hash = hashlib.md5(project_root.encode('utf-8')).hexdigest()
    port_file = Path(tempfile.gettempdir()) / (path_hash + PORT_FILE_SUFFIX)

    return int(port_file.read_text("utf-8").strip())


def send_diff_to_webserver(project_root, file_path, timestamp_ms, wait_for_response):
    try:
        port = get_server_port(project_root)
    except FileNotFoundError as e:
        raise ProvenanceHookError(
            f"Could not determine API port: {e.filename} does not exist") from e
    except Exception as e:
        raise ProvenanceHookError("Could not determine API port") from e

    url = f"http://{WEBSERVER_HOST}:{port}{WEBSERVER_ENDPOINT}"

    try:
        payload = {"file_path": file_path, "timestamp": timestamp_ms}
        return http_request(
            "POST",
            WEBSERVER_HOST,
            port=port,
            location=WEBSERVER_ENDPOINT,
            body=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
            headers={'Content-Type': 'application/json'},
            timeout=0.5,
            wait_for_response=wait_for_response
        )

    except (HTTPException, OSError, ConnectionError) as e:
        raise ProvenanceHookError(
            f"Network error while sending diff to {url}") from e
    except Exception as e:
        raise ProvenanceHookError(
            f"Unknown error while sending diff to {url}") from e


def normalize_file_path(project_root, file_path):
    path = Path(file_path)
    if not path.is_absolute():
        path = Path(project_root) / path
    return str(path.resolve())


def extract_patch_paths(patch_text):
    for line in patch_text.splitlines():
        match = re.match(r"^\*\*\* (?:Add|Update|Delete) File: (.+)$", line)
        if match:
            yield match.group(1).strip()
            continue

        match = re.match(r"^\*\*\* Move to: (.+)$", line)
        if match:
            yield match.group(1).strip()


def extract_file_paths(tool_name, tool_input):
    if not isinstance(tool_input, dict):
        return []

    paths = []
    if tool_name in ["Write", "Edit", "MultiEdit"]:
        paths.append(tool_input.get('file_path', 'unknown'))
    if tool_name == "NotebookEdit":
        paths.append(tool_input.get('notebook_path', 'unknown'))

    patch_text = tool_input.get("patch") or tool_input.get("command") or tool_input.get("input")
    if isinstance(patch_text, str):
        paths.extend(extract_patch_paths(patch_text))

    return list(dict.fromkeys(path for path in paths if path and path != "unknown"))


def excepthook(type, value, traceback_):
    traceback.print_exception(type, value, traceback_, file=sys.stderr)
    sys.exit(1)


def main():
    data = json.load(sys.stdin)
    tool_name = data.get('tool_name', 'unknown')
    project_root = find_project_root(data)

    p = argparse.ArgumentParser()
    p.add_argument("--wait_for_response", default=False)
    args = p.parse_args()

    tool_input = data.get('tool_input', {})
    file_paths = extract_file_paths(tool_name, tool_input)
    if file_paths:
        timestamp_ms = int(datetime.now(timezone.utc).timestamp() * 1000)
        for file_path in file_paths:
            send_diff_to_webserver(
                project_root,
                normalize_file_path(project_root, file_path),
                timestamp_ms,
                args.wait_for_response
            )


if __name__ == "__main__":
    sys.excepthook = excepthook
    sys.exit(main())
