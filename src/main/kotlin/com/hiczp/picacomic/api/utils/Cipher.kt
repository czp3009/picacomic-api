package com.hiczp.picacomic.api.utils

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val hmacSHA256 = "HmacSHA256"

inline class HmacSHA256(private val key: String) {
    fun encrypt(plainText: String) = Mac.getInstance(hmacSHA256).apply {
        init(SecretKeySpec(key.toByteArray(), hmacSHA256))
    }.doFinal(plainText.toByteArray())!!

    @UseExperimental(ExperimentalUnsignedTypes::class)
    fun encryptToString(plainText: String) = encrypt(plainText).joinToString(separator = "") {
        it.toUByte().toString(16)
    }
}
