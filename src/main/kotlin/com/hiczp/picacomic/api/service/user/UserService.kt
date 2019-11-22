package com.hiczp.picacomic.api.service.user

import com.hiczp.caeruleum.annotation.*
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.SortType
import com.hiczp.picacomic.api.service.Thumbnail
import com.hiczp.picacomic.api.service.comic.model.Comic
import com.hiczp.picacomic.api.service.user.model.*
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

    @Put("avatar")
    suspend fun updateAvatar(@Body updateAvatarRequest: UpdateAvatarRequest): Response<Thumbnail>

    suspend fun updateAvatar(avatar: String) =
        updateAvatar(UpdateAvatarRequest(avatar))

    @Put("password")
    suspend fun updatePassword(@Body updatePasswordRequest: UpdatePasswordRequest): Response<*>

    suspend fun updatePassword(oldPassword: String, newPassword: String) =
        updatePassword(UpdatePasswordRequest(oldPassword, newPassword))

    @Put("update-id")
    suspend fun updateId(@Body updatePicaIdRequest: UpdatePicaIdRequest): Response<*>

    suspend fun updateId(email: String, name: String) =
        updateId(UpdatePicaIdRequest(email, name))

    @Put("profile")
    suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): Response<*>

    suspend fun updateProfile(slogan: String) =
        updateProfile(UpdateProfileRequest(slogan))

    @Put("update-qa")
    suspend fun updateQuestionAndAnswer(@Body updateQuestionAndAnswerRequest: UpdateQuestionAndAnswerRequest): Response<*>

    /**
     * 总是返回 400, 不明确使用方式
     */
    @Put("{userId}/title")
    suspend fun updateTitle(
        @Path userId: String,
        @Body updateUserTitleRequest: UpdateUserTitleRequest
    ): Response<*>

    suspend fun updateTitle(userId: String, title: String) =
        updateTitle(userId, UpdateUserTitleRequest(title))
}
