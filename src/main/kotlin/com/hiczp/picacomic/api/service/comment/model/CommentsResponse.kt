package com.hiczp.picacomic.api.service.comment.model

import com.hiczp.picacomic.api.service.Page

data class CommentsResponse(
    val comments: Page<Comment>,
    val topComments: List<Comment>
)
