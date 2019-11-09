package com.hiczp.picacomic.api.service.auth

import com.hiczp.caeruleum.annotation.Attribute
import com.hiczp.caeruleum.annotation.Body
import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Post
import com.hiczp.picacomic.api.NO_AUTH
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.auth.model.*
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface AuthService {
    @Attribute(NO_AUTH)
    @Post("register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<Unit>

    @Attribute(NO_AUTH)
    @Post("sign-in")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<String>

    /**
     * 获取密保问题
     */
    @Attribute(NO_AUTH)
    @Post("forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    /**
     * 回答密保问题之一并得到一个随机密码, 使用此随机密码登陆之后再另行修改密码
     */
    @Attribute(NO_AUTH)
    @Post("reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<String>
}
