package com.hiczp.picacomic.api

import com.hiczp.picacomic.api.model.ImageInfo

public fun ImageInfo.url(): String = buildImageUrl(fileServer, path)

public fun ImageInfo.routedUrl(targetDomain: String): String =
    buildRoutedImageUrl(fileServer, path, targetDomain)

public fun ImageInfo.categoryUrl(targetDomain: String): String {
    val server = if (fileServer.contains("file.bidobido.xyz")) {
        "https://s3.picacomic.com/static"
    } else {
        fileServer
    }
    return buildRoutedImageUrl(server, path, targetDomain, routeGate = fileServer)
}

public fun buildImageUrl(fileServer: String, path: String): String {
    if (fileServer.isBlank() && (path.startsWith("file://") || path.startsWith("content://"))) {
        return path
    }
    val base = when {
        fileServer.contains("/static") -> fileServer
        fileServer.endsWith('/') -> fileServer + "static"
        else -> "$fileServer/static"
    }
    val combined = "$base/${path.trimStart('/')}"
    if (combined.contains("ad-channel", ignoreCase = true) ||
        combined.contains("pica-ad-api", ignoreCase = true)
    ) {
        return combined
    }
    val httpsPlaceholder = "__PICA_HTTPS__"
    val httpPlaceholder = "__PICA_HTTP__"
    var normalized = combined
        .replace("https://", httpsPlaceholder)
        .replace("http://", httpPlaceholder)
        .replace("//", "/")
        .replace(httpsPlaceholder, "https://")
        .replace(httpPlaceholder, "http://")
    if (!normalized.contains("picacomic.com")) {
        normalized = normalized.replace("/static/static/", "/static/")
    }
    return normalized
}

public fun buildRoutedImageUrl(
    fileServer: String,
    path: String,
    targetDomain: String,
    routeGate: String = fileServer,
): String {
    var result = buildImageUrl(fileServer, path)
    val shouldRoute = listOf("picacomic.com", "manhuabika.com", "diwodiwo.xyz")
        .any { routeGate.contains(it, ignoreCase = true) }
    if (!shouldRoute) return result
    result = result.replaceIgnoringCase("manhuabika.com", targetDomain)
    result = result.replaceIgnoringCase("diwodiwo.xyz", "storage1.$targetDomain")
    result = result.replaceIgnoringCase("picacomic.com", targetDomain)
    return result
}

private fun String.replaceIgnoringCase(oldValue: String, newValue: String): String =
    Regex(Regex.escape(oldValue), RegexOption.IGNORE_CASE).replace(this, newValue)
