package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie_downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<MovieDownloadEntity>>

    @Query("SELECT * FROM movie_downloads WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedMovies(): Flow<List<MovieDownloadEntity>>

    @Query("SELECT * FROM movie_downloads WHERE id = :id LIMIT 1")
    suspend fun getDownloadById(id: String): MovieDownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(download: MovieDownloadEntity)

    @Query("UPDATE movie_downloads SET downloadProgress = :progress, downloadSpeed = :speed, status = :status WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, speed: String, status: String)

    @Delete
    suspend fun deleteDownload(download: MovieDownloadEntity)

    @Query("DELETE FROM movie_downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: String)
}
