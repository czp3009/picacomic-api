package com.hiczp.picacomic.api.service.init.model

import com.hiczp.picacomic.api.service.Page

data class ApplicationPagesResponse(
    val applications: Page<LatestApplication>,
    val apiLevel: Int,
    val minApiLevel: Int
)
