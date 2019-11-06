package com.hiczp.picacomic.api.utils

import kotlin.random.Random

internal fun Random.nextString(charTable: List<Char>, length: Int) =
    generateSequence {
        nextInt(charTable.size)
    }.take(length).map {
        charTable[it]
    }.joinToString(separator = "")
