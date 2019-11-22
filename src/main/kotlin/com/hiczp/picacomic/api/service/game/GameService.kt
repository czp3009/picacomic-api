package com.hiczp.picacomic.api.service.game

import com.hiczp.caeruleum.annotation.*
import com.hiczp.picacomic.api.service.Page
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.comic.model.CommentRequest
import com.hiczp.picacomic.api.service.comic.model.SendCommentResponse
import com.hiczp.picacomic.api.service.comment.model.CommentsResponse
import com.hiczp.picacomic.api.service.game.model.Game
import com.hiczp.picacomic.api.service.game.model.GameDetail
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface GameService {
    @Get("/games")
    suspend fun getGames(@Query page: Int = 1): Response<Page<Game>>

    @Get("{gameId}")
    suspend fun getDetail(@Path gameId: String): Response<GameDetail>

    @Get("{gameId}/comments")
    suspend fun getComments(@Path gameId: String, @Query page: Int = 1): Response<CommentsResponse>

    @Post("{gameId}/comments")
    suspend fun sendComment(@Path gameId: String, @Body commentRequest: CommentRequest): Response<SendCommentResponse>

    suspend fun sendComment(gameId: String, content: String) =
        sendComment(gameId, CommentRequest(content))

    @Post("{gameId}/like")
    suspend fun like(@Path gameId: String): Response<String>
}
