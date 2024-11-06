package com.shub39.rush.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artists: String,
    val album: String?,
    val artUrl: String?,
    val sourceUrl: String,
    val lyrics: String,
    val syncedLyrics: String?,
    val geniusLyrics: String?
)