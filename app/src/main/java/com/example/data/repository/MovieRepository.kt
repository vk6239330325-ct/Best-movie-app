package com.example.data.repository

import com.example.data.database.MovieDao
import com.example.data.database.MovieDownloadEntity
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allDownloads: Flow<List<MovieDownloadEntity>> = movieDao.getAllDownloads()
    val bookmarkedMovies: Flow<List<MovieDownloadEntity>> = movieDao.getBookmarkedMovies()

    suspend fun getDownloadById(id: String): MovieDownloadEntity? {
        return movieDao.getDownloadById(id)
    }

    suspend fun insertOrUpdate(download: MovieDownloadEntity) {
        movieDao.insertOrUpdate(download)
    }

    suspend fun updateProgress(id: String, progress: Int, speed: String, status: String) {
        movieDao.updateProgress(id, progress, speed, status)
    }

    suspend fun deleteDownload(download: MovieDownloadEntity) {
        movieDao.deleteDownload(download)
    }

    suspend fun deleteDownloadById(id: String) {
        movieDao.deleteDownloadById(id)
    }
}
