package com.shub39.rush.network.model

data class LyricsResponse(
    val lyrics: String,
    val subtitles: String,
    val trackId: Long
)

data class SearchResponse(
    val items: List<TrackItem>,
    val totalNumberOfItems: Int
)

data class TrackItem(
    val id: Long,
    val title: String,
    val duration: Int,
    val artist: Artist,
    val album: Album,
    val url: String
)

data class Artist(
    val id: Long,
    val name: String,
    val type: String,
    val picture: String
)

data class Album(
    val id: Long,
    val title: String,
    val cover: String,
    val vibrantColor: String?
)