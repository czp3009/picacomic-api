package com.hiczp.picacomic.api.service.user

import com.hiczp.caeruleum.annotation.Get
import com.hiczp.caeruleum.annotation.Query
import com.hiczp.picacomic.api.service.CommonResponse
import com.hiczp.picacomic.api.service.SortType
import com.hiczp.picacomic.api.service.comic.model.ComicList
import com.hiczp.picacomic.api.service.user.model.UserProfile

interface UserService {
    @Get("profile")
    suspend fun getProfile(): CommonResponse<UserProfile>

    @Get("favourite")
    suspend fun getFavourite(@Query("s") sortType: SortType = SortType.DEPRECIATION_DESC, @Query page: Int = 1): CommonResponse<ComicList>
}
