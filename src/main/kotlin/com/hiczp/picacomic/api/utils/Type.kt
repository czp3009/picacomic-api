package com.hiczp.picacomic.api.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal val Type.actualTypeArguments get() = (this as ParameterizedType).actualTypeArguments
