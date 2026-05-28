package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.MovieDownloadEntity
import com.example.data.model.Movie
import com.example.data.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class LeakRequest(
    val id: String,
    val title: String,
    val industry: String,
    val requestedQuality: String,
    val status: String, // "PENDING VERIFICATION", "SCRAPING LINKS", "MIRRORS ACTIVE", "COMPLETED"
    val timestamp: Long = System.currentTimeMillis()
)

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _industryFilter = MutableStateFlow("All") // "All", "Bollywood", "Punjabi", "Downloads"
    val industryFilter: StateFlow<String> = _industryFilter.asStateFlow()

    // Simulated Leak Requests
    private val _leakRequests = MutableStateFlow<List<LeakRequest>>(
        listOf(
            LeakRequest("req_1", "Pushpa 2: The Rule", "Bollywood", "4K Ultra-HD Hybrid", "MIRRORS ACTIVE"),
            LeakRequest("req_2", "Rabb Da Radio 3", "Punjabi", "1080p WebRip DD 5.1", "SCRAPING LINKS")
        )
    )
    val leakRequests: StateFlow<List<LeakRequest>> = _leakRequests.asStateFlow()

    // All movies state
    val availableMovies = MutableStateFlow(Movie.curatedCatalog)

    // Filtered movies based on search query AND industry tab
    val filteredMovies: StateFlow<List<Movie>> = combine(
        availableMovies,
        _searchQuery,
        _industryFilter
    ) { movies, query, filter ->
        movies.filter { movie ->
            val matchesQuery = movie.title.contains(query, ignoreCase = true) ||
                    movie.genres.any { it.contains(query, ignoreCase = true) } ||
                    movie.statusBadge.contains(query, ignoreCase = true)
            
            val matchesFilter = when (filter) {
                "All" -> true
                "Bollywood" -> movie.industry == "Bollywood"
                "Punjabi" -> movie.industry == "Punjabi"
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Movie.curatedCatalog)

    // Downloads from Database
    val downloads: StateFlow<List<MovieDownloadEntity>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tracks ongoing download simulations to prevent duplicated loops
    private val activeDownloadJobs = mutableMapOf<String, Job>()

    init {
        viewModelScope.launch {
            if (repository.allDownloads.first().isEmpty()) {
                val seed1 = MovieDownloadEntity(
                    id = "stree_2_1080p_high_speed_direct",
                    movieId = "stree_2",
                    title = "Stree 2: The Terror Returns",
                    posterUrl = "",
                    resolution = "1080p High Speed Direct",
                    fileSize = "2.8 GB",
                    downloadProgress = 100,
                    status = "COMPLETED",
                    downloadSpeed = "Finished",
                    isBookmarked = false,
                    timestamp = System.currentTimeMillis() - 600000
                )
                repository.insertOrUpdate(seed1)

                val seed2 = MovieDownloadEntity(
                    id = "jatt_juliet_3_4k_uhd_webrip_native",
                    movieId = "jatt_juliet_3",
                    title = "Jatt & Juliet 3",
                    posterUrl = "",
                    resolution = "4K UHD WebRip Native",
                    fileSize = "3.8 GB",
                    downloadProgress = 42,
                    status = "DOWNLOADING",
                    downloadSpeed = "74.8 MB/s",
                    isBookmarked = false,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertOrUpdate(seed2)
                
                // Prompt automatic resume for the downloading seed
                pauseOrResumeDownload(seed2.copy(status = "PAUSED"))
            } else {
                // Resume ongoing downloader loops
                repository.allDownloads.first().forEach { item ->
                    if (item.status == "DOWNLOADING") {
                        pauseOrResumeDownload(item.copy(status = "PAUSED"))
                    }
                }
            }
        }
    }

    fun selectMovie(movie: Movie?) {
        _selectedMovie.value = movie
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateIndustryFilter(filter: String) {
        _industryFilter.value = filter
    }

    // Requests a leak
    fun submitLeakRequest(title: String, industry: String, quality: String) {
        if (title.isBlank()) return
        val newRequest = LeakRequest(
            id = "req_${System.currentTimeMillis()}",
            title = title,
            industry = industry,
            requestedQuality = quality,
            status = "PENDING VERIFICATION"
        )
        _leakRequests.value = listOf(newRequest) + _leakRequests.value

        // Simulate status evolution
        viewModelScope.launch {
            delay(5000)
            updateRequestStatus(newRequest.id, "SCRAPING LINKS")
            delay(8000)
            updateRequestStatus(newRequest.id, "MIRRORS RUNNING • DECRYPTING")
        }
    }

    private fun updateRequestStatus(id: String, status: String) {
        _leakRequests.value = _leakRequests.value.map {
            if (it.id == id) it.copy(status = status) else it
        }
    }

    // Simulate multi-threaded direct high-speed download with UI feedback & local storage save
    fun startSimulatedDownload(movie: Movie, selectedResolution: String) {
        val downloadId = "${movie.id}_${selectedResolution.replace(" ", "_").lowercase()}"
        
        // Skip if already completed or active
        if (activeDownloadJobs.containsKey(downloadId)) return

        val job = viewModelScope.launch {
            val size = when {
                selectedResolution.contains("4K", ignoreCase = true) -> "5.4 GB"
                selectedResolution.contains("1080p", ignoreCase = true) -> "2.2 GB"
                else -> movie.fileSize
            }

            var download = MovieDownloadEntity(
                id = downloadId,
                movieId = movie.id,
                title = movie.title,
                posterUrl = "", // Coil placeholder handles backdrop color fallback
                resolution = selectedResolution,
                fileSize = size,
                downloadProgress = 0,
                status = "DOWNLOADING",
                downloadSpeed = "0.0 MB/s",
                isBookmarked = false
            )
            repository.insertOrUpdate(download)

            var progress = 0
            while (progress < 100) {
                delay(800)
                progress += Random.nextInt(7, 18)
                if (progress > 100) progress = 100
                
                val currentSpeed = String.format("%.1f MB/s", Random.nextDouble(55.2, 112.4))
                val isDone = progress == 100
                
                download = download.copy(
                    downloadProgress = progress,
                    status = if (isDone) "COMPLETED" else "DOWNLOADING",
                    downloadSpeed = if (isDone) "Finished" else currentSpeed
                )
                repository.insertOrUpdate(download)
            }
            activeDownloadJobs.remove(downloadId)
        }
        activeDownloadJobs[downloadId] = job
    }

    fun pauseOrResumeDownload(download: MovieDownloadEntity) {
        val downloadId = download.id
        if (download.status == "DOWNLOADING") {
            // Cancel job
            activeDownloadJobs[downloadId]?.cancel()
            activeDownloadJobs.remove(downloadId)
            
            viewModelScope.launch {
                repository.insertOrUpdate(download.copy(status = "PAUSED", downloadSpeed = "Paused"))
            }
        } else if (download.status == "PAUSED") {
            // Resume: lookup original movie from catalog & restart simulation
            val matchedMovie = Movie.curatedCatalog.firstOrNull { it.id == download.movieId }
            if (matchedMovie != null) {
                // Restart simulated coroutine from existing progress
                val job = viewModelScope.launch {
                    var progress = download.downloadProgress
                    var currentDownload = download.copy(status = "DOWNLOADING", downloadSpeed = "Connecting...")
                    repository.insertOrUpdate(currentDownload)
                    delay(500)

                    while (progress < 100) {
                        delay(800)
                        progress += Random.nextInt(8, 20)
                        if (progress > 100) progress = 100
                        val currentSpeed = String.format("%.1f MB/s", Random.nextDouble(62.5, 118.9))
                        val isDone = progress == 100
                        
                        currentDownload = currentDownload.copy(
                            downloadProgress = progress,
                            status = if (isDone) "COMPLETED" else "DOWNLOADING",
                            downloadSpeed = if (isDone) "Finished" else currentSpeed
                        )
                        repository.insertOrUpdate(currentDownload)
                    }
                    activeDownloadJobs.remove(downloadId)
                }
                activeDownloadJobs[downloadId] = job
            }
        }
    }

    fun deleteDownload(downloadId: String) {
        activeDownloadJobs[downloadId]?.cancel()
        activeDownloadJobs.remove(downloadId)
        viewModelScope.launch {
            repository.deleteDownloadById(downloadId)
        }
    }
}

class ViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
