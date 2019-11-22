package com.hiczp.picacomic.api.service.util

import com.hiczp.caeruleum.annotation.Body
import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Post
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.UserIdRequest
import com.hiczp.picacomic.api.service.util.model.AdjustExpRequest
import com.hiczp.picacomic.api.utils.JSON_UTF8

/**
 * 一些接口总是返回 404, 可能已废弃
 */
@DefaultContentType(JSON_UTF8)
interface UtilService {
    @Post("adjust-exp")
    suspend fun adjustExp(@Body adjustExpRequest: AdjustExpRequest): Response<*>

    suspend fun adjustExp(userId: String, exp: Int) =
        adjustExp(AdjustExpRequest(userId, exp))

    @Post("remove-comment")
    suspend fun removeComment(@Body userIdRequest: UserIdRequest): Response<*>

    suspend fun removeComment(userId: String) =
        removeComment(UserIdRequest(userId))

    @Post("block-user")
    suspend fun blockUser(@Body userIdRequest: UserIdRequest): Response<*>

    suspend fun blockUser(userId: String) =
        blockUser(UserIdRequest(userId))
}
