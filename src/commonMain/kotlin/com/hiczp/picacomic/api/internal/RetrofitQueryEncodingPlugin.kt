package com.hiczp.picacomic.api.internal

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath

internal val RetrofitEncodingPlugin = createClientPlugin("RetrofitEncodingPlugin") {
    onRequest { request, _ -> normalizeRetrofitEncoding(request.url) }
}

internal fun normalizeRetrofitEncoding(url: URLBuilder) {
    val normalizedParameters = ParametersBuilder()
    url.encodedParameters.entries().forEach { (encodedName, encodedValues) ->
        normalizedParameters.appendAll(
            encodedName.replace("+", "%20"),
            encodedValues.map { it.replace("+", "%20") },
        )
    }
    url.encodedParameters = normalizedParameters
    url.encodedPath = normalizeKnownPathParameters(url.encodedPath)
}

private fun normalizeKnownPathParameters(path: String): String {
    val segments = path.removePrefix("/").split('/')
    if (segments.isEmpty()) return path
    return when (segments.first()) {
        "comics" -> normalizeComicPath(path, segments)
        "comments" -> normalizeSingleIdentifierPath(path, segments, setOf("childrens", "like", "report"))
        "games" -> normalizeSingleIdentifierPath(path, segments, setOf("comments", "like"))
        "picacomic" -> {
            if (segments.getOrNull(1) == "rec" && segments.size > 3) {
                "/picacomic/rec/${segments.drop(2).joinToString("%2F")}"
            } else {
                path
            }
        }

        else -> path
    }
}

private fun normalizeComicPath(path: String, segments: List<String>): String {
    if (segments.size <= 2 && segments.getOrNull(1) in setOf(null, "advanced-search", "random", "leaderboard")) {
        return path
    }
    if (segments.lastOrNull() == "pages") {
        val orderMarker = segments.indexOfLast { it == "order" }
        if (orderMarker > 1 && orderMarker < segments.lastIndex) {
            val comicId = segments.subList(1, orderMarker).joinToString("%2F")
            val order = segments.subList(orderMarker + 1, segments.lastIndex).joinToString("%2F")
            return "/comics/$comicId/order/$order/pages"
        }
    }
    val suffix = segments.lastOrNull()?.takeIf { it in setOf("favourite", "like", "eps", "comments") }
    return if (suffix != null && segments.size > 3) {
        "/comics/${segments.subList(1, segments.lastIndex).joinToString("%2F")}/$suffix"
    } else if (suffix == null && segments.size > 2) {
        "/comics/${segments.drop(1).joinToString("%2F")}"
    } else {
        path
    }
}

private fun normalizeSingleIdentifierPath(
    path: String,
    segments: List<String>,
    suffixes: Set<String>,
): String {
    val suffix = segments.lastOrNull()?.takeIf(suffixes::contains)
    return if (suffix != null && segments.size > 3) {
        "/${segments.first()}/${segments.subList(1, segments.lastIndex).joinToString("%2F")}/$suffix"
    } else if (suffix == null && segments.size > 2) {
        "/${segments.first()}/${segments.drop(1).joinToString("%2F")}"
    } else {
        path
    }
}
