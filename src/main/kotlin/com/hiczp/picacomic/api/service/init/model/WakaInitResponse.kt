package com.hiczp.picacomic.api.service.init.model

@Suppress("SpellCheckingInspection")
data class WakaInitResponse(
    val status: String, // ok
    val addresses: List<String>,
    val waka: String, // https://ad-channel.woyeahgo.tk
    val adKeyword: String // woyeahgo
)
