package com.shub39.rush.network.api

import com.shub39.rush.network.model.LyricsResponse
import com.shub39.rush.network.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FluidacApi {
    @GET("api/lyrics/")
    fun getLyrics(@Query("id") id: Long): Call<LyricsResponse>

    @GET("api/search/tracks/")
    fun search(@Query("query") query: String): Call<SearchResponse>
}