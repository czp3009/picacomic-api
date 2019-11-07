package com.hiczp.picacomic.api.service.auth

import com.hiczp.caeruleum.annotation.Attribute
import com.hiczp.caeruleum.annotation.Body
import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Post
import com.hiczp.picacomic.api.NO_AUTH
import com.hiczp.picacomic.api.service.CommonResponse
import com.hiczp.picacomic.api.service.auth.model.RegisterRequest
import com.hiczp.picacomic.api.service.auth.model.SignInRequest
import com.hiczp.picacomic.api.service.auth.model.SignInResponse
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface AuthService {
    @Attribute(NO_AUTH)
    @Post("register")
    suspend fun register(@Body registerRequest: RegisterRequest): CommonResponse<Unit>

    @Attribute(NO_AUTH)
    @Post("sign-in")
    suspend fun signIn(@Body signInRequest: SignInRequest): CommonResponse<SignInResponse>
}
