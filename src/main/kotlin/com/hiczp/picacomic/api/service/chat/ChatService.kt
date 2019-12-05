package com.hiczp.picacomic.api.service.chat

import com.hiczp.caeruleum.annotation.DefaultContentType
import com.hiczp.caeruleum.annotation.Get
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.chat.model.ChatRoom
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface ChatService {
    @Get
    suspend fun getChatRooms(): Response<List<ChatRoom>>
}
