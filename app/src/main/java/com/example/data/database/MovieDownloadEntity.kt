package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_downloads")
data class MovieDownloadEntity(
    @PrimaryKey val id: String, // format: "movieId_resolution" or just "movieId"
    val movieId: String,
    val title: String,
    val posterUrl: String,
    val resolution: String,
    val fileSize: String,
    val downloadProgress: Int, // 0 to 100
    val status: String, // "DOWNLOADING", "PAUSED", "COMPLETED", "FAILED"
    var downloadSpeed: String, // e.g., "78.4 MB/s"
    val isBookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
