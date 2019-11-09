package com.hiczp.picacomic.api.service.user.model

data class PunchInResponse(
    val punchInLastDay: String?,
    val status: String
) {
    val success get() = status != "fail"
}
