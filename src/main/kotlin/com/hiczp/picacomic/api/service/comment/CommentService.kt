package com.hiczp.picacomic.api.service.comment

import com.hiczp.caeruleum.annotation.*
import com.hiczp.picacomic.api.service.Response
import com.hiczp.picacomic.api.service.comic.model.SendCommentResponse
import com.hiczp.picacomic.api.service.comment.model.CommentRequest
import com.hiczp.picacomic.api.service.comment.model.CommentsResponse
import com.hiczp.picacomic.api.utils.JSON_UTF8

@DefaultContentType(JSON_UTF8)
interface CommentService {
    /**
     * 获取一个评论的回复
     */
    @Get("{commentId}/childrens")
    suspend fun children(
        @Path commentId: String,
        @Query page: Int = 1
    ): Response<CommentsResponse>

    /**
     * 回复一个评论
     */
    @Post("{commentId}")
    suspend fun reply(
        @Path commentId: String,
        @Body commentRequest: CommentRequest
    ): Response<SendCommentResponse>

    suspend fun reply(commentId: String, content: String) =
        reply(commentId, CommentRequest(content))

    @Post("{commentId}/like")
    suspend fun like(@Path commentId: String): Response<String>

    @Post("{commentId}/hide")
    suspend fun hide(@Path commentId: String): Response<String>

    @Post("{commentId}/report")
    suspend fun report(@Path commentId: String): Response<String>

    @Post("{commentId}/top")
    suspend fun top(@Path commentId: String): Response<Boolean>
}
