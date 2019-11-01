package com.hiczp.picacomic.api.service.main.model

data class InitResponse(
    val status: String, // ok
    val addresses: List<String>,
    val waka: String, // https://ad-channel.woyeahgo.tk
    val adKeyword: String // woyeahgo
)
