package com.hiczp.picacomic.api.service.init.model

import com.hiczp.picacomic.api.service.IdAndTitle
import com.hiczp.picacomic.api.service.user.model.Notification

data class InitResponse(
    val isPunched: Boolean,
    val latestApplication: LatestApplication,
    val imageServer: String,
    val apiLevel: Int,
    val minApiLevel: Int,
    val categories: List<IdAndTitle>,
    val notification: Notification?,
    val isIdUpdated: Boolean
)
