package com.hiczp.picacomic.api.utils

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val algorithm = "HmacSHA256"

private typealias MacResult = ByteArray

internal fun hmacSHA256(key: String, data: String) =
    Mac.getInstance(algorithm).apply {
        init(SecretKeySpec(key.toByteArray(), algorithm))
    }.doFinal(data.toByteArray()) as MacResult

@Suppress("SpellCheckingInspection")
private val hexTable = "0123456789abcdef".toCharArray()

@UseExperimental(ExperimentalUnsignedTypes::class)
internal fun MacResult.convertToString() = buildString(size * 2) {
    this@convertToString.forEach {
        val value = it.toUByte().toInt()
        append(hexTable[value ushr 4])
        append(hexTable[value and 0x0f])
    }
}
