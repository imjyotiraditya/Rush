package com.shub39.rush.network

import android.util.Log
import com.shub39.rush.database.SearchResult
import com.shub39.rush.database.Song
import com.shub39.rush.error.Result
import com.shub39.rush.error.SourceError
import com.shub39.rush.network.api.FluidacApi
import com.shub39.rush.network.model.LyricsResponse
import com.shub39.rush.network.model.SearchResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object SongProvider {
    private const val TAG = "SongProvider"

    private const val BASE_URL = "https://fluidac-api.vercel.app/"
    private const val AUTH_HEADER = "X-Api-Key"
    private const val API_KEY = Tokens.FLUIDAC_API

    private val fluidacApi: FluidacApi

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(AUTH_HEADER, API_KEY)
                    .build()
                chain.proceed(request)
            }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fluidacApi = retrofit.create(FluidacApi::class.java)
    }

    private var lastSearchResults: Map<Long, SearchResult> = emptyMap()

    fun search(query: String): Result<List<SearchResult>> {
        return try {
            val response: Response<SearchResponse> = fluidacApi.search(query).execute()

            if (response.isSuccessful) {
                val searchResponse = response.body()
                if (searchResponse == null || searchResponse.items.isEmpty()) {
                    Result.Error(SourceError.Data.NO_RESULTS)
                } else {
                    val results = searchResponse.items.map { item ->
                        SearchResult(
                            id = item.id,
                            title = item.title,
                            artist = item.artist.name,
                            album = item.album.title,
                            artUrl = "https://resources.tidal.com/images/${
                                item.album.cover.replace(
                                    "-",
                                    "/"
                                )
                            }/1280x1280.jpg",
                            url = item.url
                        )
                    }
                    lastSearchResults = results.associateBy { it.id }
                    Result.Success(results)
                }
            } else {
                Log.e(TAG, "Search failed: ${response.code()} ${response.message()}")
                Result.Error(SourceError.Network.REQUEST_FAILED)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during search", e)
            Result.Error(SourceError.Network.NO_INTERNET)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during search", e)
            Result.Error(SourceError.Data.UNKNOWN)
        }
    }


    fun fetchLyrics(songId: Long): Result<Song> {
        return try {
            val response: Response<LyricsResponse> = fluidacApi.getLyrics(songId).execute()

            if (response.isSuccessful) {
                val lyricsResponse = response.body()
                Log.e(TAG, lyricsResponse.toString())
                if (lyricsResponse == null) {
                    Result.Error(SourceError.Data.NO_RESULTS)
                } else {
                    val songInfo = lastSearchResults[songId]
                        ?: return Result.Error(SourceError.Data.NO_RESULTS)

                    Result.Success(
                        Song(
                            id = songId,
                            title = songInfo.title,
                            artists = songInfo.artist,
                            album = songInfo.album,
                            artUrl = songInfo.artUrl,
                            sourceUrl = songInfo.url,
                            lyrics = lyricsResponse.lyrics,
                            syncedLyrics = lyricsResponse.subtitles,
                            geniusLyrics = null
                        )
                    )
                }
            } else {
                Log.e(TAG, "Lyrics fetch failed: ${response.code()} ${response.message()}")
                Result.Error(SourceError.Network.REQUEST_FAILED)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during lyrics fetch", e)
            Result.Error(SourceError.Network.NO_INTERNET)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during lyrics fetch", e)
            Result.Error(SourceError.Data.UNKNOWN)
        }
    }
}