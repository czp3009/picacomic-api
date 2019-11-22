package com.hiczp.picacomic.api.service.user.model

import com.google.gson.annotations.SerializedName

data class UpdatePasswordRequest(
    @field:SerializedName("old_password")
    val oldPassword: String,
    @field:SerializedName("new_password")
    val newPassword: String
)
