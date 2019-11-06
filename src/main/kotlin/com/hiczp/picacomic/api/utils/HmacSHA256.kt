package com.hiczp.picacomic.api.utils

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val algorithm = "HmacSHA256"

private typealias MacResult = ByteArray

fun hmacSHA256(key: String, data: String) =
    Mac.getInstance(algorithm).apply {
        init(SecretKeySpec(key.toByteArray(), algorithm))
    }.doFinal(data.toByteArray()) as MacResult

@UseExperimental(ExperimentalUnsignedTypes::class)
fun MacResult.convertToString() = joinToString(separator = "") {
    it.toUByte().toString(16)
}
