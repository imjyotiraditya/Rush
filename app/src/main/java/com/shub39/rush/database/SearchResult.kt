package com.shub39.rush.database

data class SearchResult(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val artUrl: String,
    val url: String
)