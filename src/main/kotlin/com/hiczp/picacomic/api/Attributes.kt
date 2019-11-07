package com.hiczp.picacomic.api

import io.ktor.util.AttributeKey
import io.ktor.util.Attributes

internal const val NO_SIGN = "NoSign"

internal val noSignAttribute = AttributeKey<String>(NO_SIGN)

internal const val NO_AUTH = "NoAuth"

internal val noAuthAttribute = AttributeKey<String>(NO_AUTH)

internal fun Attributes.containsByKey(attributeKey: AttributeKey<*>) = allKeys.any { it.name == attributeKey.name }
