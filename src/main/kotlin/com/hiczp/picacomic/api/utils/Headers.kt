package com.hiczp.picacomic.api.utils

import io.ktor.http.HeadersBuilder

internal fun HeadersBuilder.removeAndAdd(name: String, value: String) {
    remove(name)
    append(name, value)
}
