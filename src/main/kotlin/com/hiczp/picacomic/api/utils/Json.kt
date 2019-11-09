package com.hiczp.picacomic.api.utils

import com.google.gson.JsonObject

internal val JsonObject.firstElement get() = entrySet().firstOrNull()?.value
