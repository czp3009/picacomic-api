package com.hiczp.picacomic.api.internal

import com.hiczp.picacomic.api.PicaConstants

internal object PicaSigner {
    private const val HMAC_KEY: String =
        "~d}\$Q7\$eIni=V)9\\RK/P.RM4;9[7|@/CA}b~OW!3?EV`:<>M7pddUBL5n|0/*Cn"

    fun sign(
        encodedPath: String,
        encodedQuery: String,
        time: String,
        nonce: String,
        method: String,
    ): String {
        val path = encodedPath.removePrefix("/")
        val canonical = if (encodedQuery.isEmpty()) path else "$path?$encodedQuery"
        val message = (canonical + time + nonce + method + PicaConstants.API_KEY).lowercase()
        return hmacSha256(HMAC_KEY.encodeToByteArray(), message.encodeToByteArray()).toHex()
    }

    private fun hmacSha256(key: ByteArray, message: ByteArray): ByteArray {
        val normalized = if (key.size > 64) sha256(key) else key
        val innerPad = ByteArray(64) { 0x36 }
        val outerPad = ByteArray(64) { 0x5c }
        normalized.forEachIndexed { index, byte ->
            innerPad[index] = (innerPad[index].toInt() xor byte.toInt()).toByte()
            outerPad[index] = (outerPad[index].toInt() xor byte.toInt()).toByte()
        }
        return sha256(outerPad + sha256(innerPad + message))
    }

    private fun sha256(input: ByteArray): ByteArray {
        val bitLength = input.size.toLong() * 8L
        val paddingBytes = (56 - (input.size + 1) % 64 + 64) % 64
        val padded = ByteArray(input.size + 1 + paddingBytes + 8)
        input.copyInto(padded)
        padded[input.size] = 0x80.toByte()
        repeat(8) { index ->
            padded[padded.lastIndex - index] = (bitLength ushr (index * 8)).toByte()
        }

        var h0 = 0x6a09e667
        var h1 = 0xbb67ae85.toInt()
        var h2 = 0x3c6ef372
        var h3 = 0xa54ff53a.toInt()
        var h4 = 0x510e527f
        var h5 = 0x9b05688c.toInt()
        var h6 = 0x1f83d9ab
        var h7 = 0x5be0cd19
        val words = IntArray(64)

        for (offset in padded.indices step 64) {
            for (index in 0 until 16) {
                val cursor = offset + index * 4
                words[index] =
                    ((padded[cursor].toInt() and 0xff) shl 24) or
                            ((padded[cursor + 1].toInt() and 0xff) shl 16) or
                            ((padded[cursor + 2].toInt() and 0xff) shl 8) or
                            (padded[cursor + 3].toInt() and 0xff)
            }
            for (index in 16 until 64) {
                val s0 = rotateRight(words[index - 15], 7) xor rotateRight(words[index - 15], 18) xor
                        (words[index - 15] ushr 3)
                val s1 = rotateRight(words[index - 2], 17) xor rotateRight(words[index - 2], 19) xor
                        (words[index - 2] ushr 10)
                words[index] = words[index - 16] + s0 + words[index - 7] + s1
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4
            var f = h5
            var g = h6
            var h = h7
            for (index in 0 until 64) {
                val s1 = rotateRight(e, 6) xor rotateRight(e, 11) xor rotateRight(e, 25)
                val choice = (e and f) xor (e.inv() and g)
                val temp1 = h + s1 + choice + SHA256_CONSTANTS[index] + words[index]
                val s0 = rotateRight(a, 2) xor rotateRight(a, 13) xor rotateRight(a, 22)
                val majority = (a and b) xor (a and c) xor (b and c)
                val temp2 = s0 + majority
                h = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }
            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            h5 += f
            h6 += g
            h7 += h
        }

        return intArrayOf(h0, h1, h2, h3, h4, h5, h6, h7).flatMapToBytes()
    }

    private fun rotateRight(value: Int, bits: Int): Int = (value ushr bits) or (value shl (32 - bits))

    private fun IntArray.flatMapToBytes(): ByteArray = ByteArray(size * 4).also { output ->
        forEachIndexed { index, value ->
            output[index * 4] = (value ushr 24).toByte()
            output[index * 4 + 1] = (value ushr 16).toByte()
            output[index * 4 + 2] = (value ushr 8).toByte()
            output[index * 4 + 3] = value.toByte()
        }
    }

    private fun ByteArray.toHex(): String {
        val alphabet = "0123456789abcdef"
        return buildString(size * 2) {
            for (byte in this@toHex) {
                val value = byte.toInt() and 0xff
                append(alphabet[value ushr 4])
                append(alphabet[value and 0x0f])
            }
        }
    }

    private val SHA256_CONSTANTS: IntArray = intArrayOf(
        0x428a2f98, 0x71374491, 0xb5c0fbcf.toInt(), 0xe9b5dba5.toInt(),
        0x3956c25b, 0x59f111f1, 0x923f82a4.toInt(), 0xab1c5ed5.toInt(),
        0xd807aa98.toInt(), 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe.toInt(), 0x9bdc06a7.toInt(), 0xc19bf174.toInt(),
        0xe49b69c1.toInt(), 0xefbe4786.toInt(), 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152.toInt(), 0xa831c66d.toInt(), 0xb00327c8.toInt(), 0xbf597fc7.toInt(),
        0xc6e00bf3.toInt(), 0xd5a79147.toInt(), 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e.toInt(), 0x92722c85.toInt(),
        0xa2bfe8a1.toInt(), 0xa81a664b.toInt(), 0xc24b8b70.toInt(), 0xc76c51a3.toInt(),
        0xd192e819.toInt(), 0xd6990624.toInt(), 0xf40e3585.toInt(), 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814.toInt(), 0x8cc70208.toInt(),
        0x90befffa.toInt(), 0xa4506ceb.toInt(), 0xbef9a3f7.toInt(), 0xc67178f2.toInt(),
    )
}
