package com.hiczp.picacomic.api.service.user

import com.hiczp.caeruleum.annotation.*
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.SortType
import com.hiczp.picacomic.api.service.comic.model.Comic
import com.hiczp.picacomic.api.service.user.model.Notification
import com.hiczp.picacomic.api.service.user.model.ProfileComment
import com.hiczp.picacomic.api.service.user.model.PunchInResponse
import com.hiczp.picacomic.api.service.user.model.User
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface UserService {
    @Get("notifications")
    suspend fun getNotification(@Query page: Int = 1): Response<Page<Notification>>

    @Get("profile")
    suspend fun getProfile(): Response<User>

    @Get("{userId}/profile")
    suspend fun getProfile(@Path userId: String): Response<User>

    @Post("{userId}/dirty")
    suspend fun dirty(@Path userId: String): Response<Boolean>

    @Get("my-comments")
    suspend fun getMyComments(@Query page: Int = 1): Response<Page<ProfileComment>>

    @Get("favourite")
    suspend fun getFavourite(
        @Query("s") sortType: SortType = SortType.DEPRECIATION_DESC,
        @Query page: Int = 1
    ): Response<Page<Comic>>

    @Post("punch-in")
    suspend fun punchIn(): Response<PunchInResponse>
}
