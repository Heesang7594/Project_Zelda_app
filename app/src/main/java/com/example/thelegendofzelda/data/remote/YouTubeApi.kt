package com.example.thelegendofzelda.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {
    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("key") apiKey: String
    ): YouTubeSearchResponse
}

@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    @Json(name = "items") val items: List<YouTubeSearchItem>
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchItem(
    @Json(name = "id") val id: VideoId,
    @Json(name = "snippet") val snippet: VideoSnippet
)

@JsonClass(generateAdapter = true)
data class VideoId(
    @Json(name = "videoId") val videoId: String
)

@JsonClass(generateAdapter = true)
data class VideoSnippet(
    @Json(name = "title") val title: String,
    @Json(name = "thumbnails") val thumbnails: Thumbnails
)

@JsonClass(generateAdapter = true)
data class Thumbnails(
    @Json(name = "high") val high: ThumbnailInfo
)

@JsonClass(generateAdapter = true)
data class ThumbnailInfo(
    @Json(name = "url") val url: String
)
