# Repository Guidelines

## Project Structure & Module Organization

This is a single-module Kotlin Multiplatform API library. Shared production code lives under
`src/commonMain/kotlin/com/hiczp/picacomic/api`. Public client configuration, exceptions, and URL helpers are in the
package root; Ktorfit declarations are in `service/`, serializable wire models are in `model/`, and signing or request
plumbing belongs in `internal/`. Platform-neutral tests mirror this layout under `src/commonTest/kotlin`. Gradle
configuration is defined in `build.gradle.kts` and `gradle/libs.versions.toml`. Treat `build/`, `.gradle/`, and
KSP-generated output as disposable artifacts; do not edit them manually.

## Build, Test, and Development Commands

Run commands from the repository root:

- `.\gradlew.bat jvmTest` runs the executable JVM test suite and is the fastest validation loop.
- `.\gradlew.bat build` compiles all configured JVM, Android, JS, Wasm, and native targets and creates artifacts.
- `.\gradlew.bat jvmSourcesJar` verifies the JVM source artifact, including generated Ktorfit implementations.
- `.\gradlew.bat clean` removes generated build output when incremental state is stale.

On Unix-like shells, use `./gradlew` instead of `gradlew.bat`. The build requires the configured JDK 17 toolchain.

## Coding Style & Naming Conventions

Follow standard Kotlin style: four-space indentation, braces on the declaration line, and trailing commas in multiline
declarations. The build enables `explicitApi()`, so public declarations need explicit visibility and types. Use
descriptive Kotlin names rather than protocol abbreviations; preserve wire names only through annotations such as
`@Query` or `@SerialName`. Put each parameter/property annotation on its own line. Keep public Ktorfit interfaces
accessible while placing orchestration, authentication, and retry behavior in `PicaComicClient`. Match model nullability
and defaults to the API contract. No automatic formatter or linter is currently configured, so review formatting before
committing.

## Testing Guidelines

Tests use `kotlin.test`, `kotlinx-coroutines-test`, and Ktor `MockEngine`. Name files `*Test.kt` and test functions
after observable behavior. Add regression coverage for serialization contracts, URL encoding, request signing,
credential transitions, retries, and host selection when those areas change. Tests must be deterministic, must not call
live services, and must not contain real credentials. Runtime tests execute on JVM; `build` additionally checks
compilation across configured targets.

## Commit & Pull Request Guidelines

History uses short, imperative subjects such as `add some API`, `optimize code`, and `maven publish`. Prefer a similarly
concise but more specific subject, for example `add anime category models` or `fix credential retry`. Pull requests
should summarize behavior and public API changes, identify compatibility concerns, link relevant issues, and list the
validation commands run. Keep generated files and unrelated cleanup out of the diff.
