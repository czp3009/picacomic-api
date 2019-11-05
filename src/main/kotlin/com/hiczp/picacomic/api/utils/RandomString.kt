package com.hiczp.picacomic.api.utils

import kotlin.random.Random

fun Random.nextString(stringLength: Int, charTable: List<Char>) =
    sequence<Int> {
        Random.nextInt(charTable.size)
    }.take(stringLength).map {
        charTable[it]
    }.joinToString(separator = "")
