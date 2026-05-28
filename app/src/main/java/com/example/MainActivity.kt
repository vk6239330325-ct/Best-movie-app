package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.MovieDatabase
import com.example.data.database.MovieDownloadEntity
import com.example.data.model.Movie
import com.example.data.repository.MovieRepository
import com.example.ui.MovieViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize local Room database & Repository
        val database = MovieDatabase.getDatabase(applicationContext)
        val repository = MovieRepository(database.movieDao)
        val factory = ViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: MovieViewModel = viewModel(factory = factory)
                
                Scaffold(
                    modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    VKMoviesDashboard(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VKMoviesDashboard(
    viewModel: MovieViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // UI states
    val filteredMovies by viewModel.filteredMovies.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val selectedMovie by viewModel.selectedMovie.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.industryFilter.collectAsStateWithLifecycle()
    val leakRequests by viewModel.leakRequests.collectAsStateWithLifecycle()

    // Dialog & overlay states
    var isStreamingMode by remember { mutableStateOf(false) }
    var showingRequestDialog by remember { mutableStateOf(false) }
    var selectedResolutionOption by remember { mutableStateOf("4K HDR Atmos Link") }

    // Stream simulation details
    var isPlayingStream by remember { mutableStateOf(false) }
    var streamProgress by remember { mutableFloatStateOf(0.12f) }
    var mockBufferProgress by remember { mutableFloatStateOf(0.35f) }

    // Launch coroutine to animate streaming playback smoothly when active
    LaunchedEffect(isPlayingStream, isStreamingMode) {
        if (isPlayingStream && isStreamingMode) {
            while (true) {
                delay(1000)
                if (streamProgress < 1.0f) {
                    streamProgress += 0.015f
                    if (mockBufferProgress < 1.0f) {
                        mockBufferProgress = (streamProgress + 0.25f).coerceAtMost(1.0f)
                    }
                } else {
                    streamProgress = 0f
                    mockBufferProgress = 0.2f
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- HEADER TITLE & P2P LIVE STATS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF13171E))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "VK",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "MOVIES",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color.White
                        )
                    }
                    Text(
                        "Speed-Direct P2P Link & Stream Hub",
                        fontSize = 11.sp,
                        color = Color(0xFF8A9AAD)
                    )
                }

                // Global Status Indicator Badge
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF2A9D8F))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "ONLINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A9D8F)
                        )
                    }
                    Text(
                        "Seeds: 247,901",
                        fontSize = 11.sp,
                        color = Color(0xFF8A9AAD)
                    )
                }
            }

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_input"),
                placeholder = { Text("Search Bollywood & Punjabi blockbusters...", color = Color(0xFF7E8F9F)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = Color.LightGray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.LightGray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B222E),
                    unfocusedContainerColor = Color(0xFF13171E),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFF232B38)
                )
            )

            // --- SECTOR CHIPS / TAB SELECTOR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("All", "All Leaks 🍿"),
                    Pair("Bollywood", "Bollywood Hits 🇮🇳"),
                    Pair("Punjabi", "Punjabi Golden 🌾"),
                    Pair("Downloads", "Active Downloads 📂")
                ).forEach { (filterKey, filterText) ->
                    val isSelected = activeFilter == filterKey
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary 
                                else Color(0xFF1B222E)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF2A3445),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.updateIndustryFilter(filterKey) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("chip_$filterKey"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filterText,
                            color = if (isSelected) Color.White else Color(0xFFC0CAD9),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- SCROLLER OR DOWNLOAD LIST CONTENT ---
            AnimatedContent(
                targetState = activeFilter,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "MainContentSwitch"
            ) { targetTab ->
                if (targetTab == "Downloads") {
                    DownloadsTabContent(
                        downloads = downloads,
                        onPauseResume = { viewModel.pauseOrResumeDownload(it) },
                        onDelete = { viewModel.deleteDownload(it) },
                        onStreamClicked = { download ->
                            // Look up if movie exists in main catalog to stream
                            val foundMovie = Movie.curatedCatalog.firstOrNull { it.id == download.movieId }
                            if (foundMovie != null) {
                                viewModel.selectMovie(foundMovie)
                                isStreamingMode = true
                                isPlayingStream = true
                                streamProgress = 0f
                            } else {
                                Toast.makeText(context, "Streaming demo loaded for files", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                } else {
                    CatalogTabContent(
                        movies = filteredMovies,
                        onMovieSelected = { movie ->
                            viewModel.selectMovie(movie)
                            // reset chosen quality defaults
                            selectedResolutionOption = when(movie.id) {
                                "stree_2" -> "4K HEVC Atmos Link"
                                "jatt_juliet_3" -> "4K UHD WebRip Native"
                                "animal_dark" -> "4K UHD HDR 10-Bit"
                                else -> "1080p High Quality Link"
                            }
                        },
                        onRequestLeakClicked = { showingRequestDialog = true }
                    )
                }
            }
        }

        // --- SUB-DIALOG / DETAILED SLIDE-UP PANEL FOR SELECTED MOVIE ---
        selectedMovie?.let { movie ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF2C3545),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .clickable(enabled = false) {}, // Intercept clicks
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13171E))
            ) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    
                    // Top Drag Bar / Close
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF8A9AAD))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = movie.industry.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                        
                        IconButton(
                            onClick = { 
                                viewModel.selectMovie(null)
                                isStreamingMode = false
                                isPlayingStream = false
                            },
                            modifier = Modifier.testTag("close_sheet_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close detailed view", tint = Color.White)
                        }
                    }

                    // Simulated Video Stream Screen or Movie Banner Hero
                    if (isStreamingMode) {
                        // --- SIMULATOR STREAM PLAYER SCREEN ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.Black)
                                .border(1.dp, Color(0xFFE50914))
                        ) {
                            // Video Background simulator loop colors
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(movie.backdropColorHex).copy(alpha = 0.35f),
                                                Color.Black
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayingStream) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(44.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "SIMULATOR WATCH: DECRYPTING STREAM SEGMENT...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            "Buffer: 100% Locked • 4K HEVC Master",
                                            fontSize = 9.sp,
                                            color = Color(0xFF2A9D8F)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { isPlayingStream = true },
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                    ) {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = "Start Stream Play",
                                            modifier = Modifier.size(40.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            // Controller overlays
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { isPlayingStream = !isPlayingStream },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            if (isPlayingStream) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play toggle",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    
                                    // Custom visual dual-bar buffer/stream progress lines
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color.White.copy(alpha = 0.30f))
                                    ) {
                                        // Buffer segment
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(mockBufferProgress)
                                                .background(Color.White.copy(alpha = 0.50f))
                                        )
                                        // Stream position segment
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(streamProgress)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${(streamProgress * 150).toInt()}:00 / 150:00",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.White
                                    )
                                }
                            }

                            // Stream details top tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "LIVE PLAYER FEED (PREVIEW MODE)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE50914)
                                )
                            }
                        }
                    } else {
                        // --- BANNER HERO ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(movie.backdropColorHex),
                                            Color(0xFF13171E)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        movie.statusBadge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE9C46A)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    movie.title,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Film Specs Metadata Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(Color(0xFF1B222E), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("IMDB RATIO", fontSize = 10.sp, color = Color(0xFF8A9AAD))
                            Text("★ ${movie.rating}/10", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Divider(modifier = Modifier.width(1.dp).height(30.dp), color = Color(0xFF2C3545))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RELEASE", fontSize = 10.sp, color = Color(0xFF8A9AAD))
                            Text(movie.rYear, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Divider(modifier = Modifier.width(1.dp).height(30.dp), color = Color(0xFF2C3545))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DURATION", fontSize = 10.sp, color = Color(0xFF8A9AAD))
                            Text(movie.duration, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Divider(modifier = Modifier.width(1.dp).height(30.dp), color = Color(0xFF2C3545))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SIZE", fontSize = 10.sp, color = Color(0xFF8A9AAD))
                            Text(movie.fileSize, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B4D8))
                        }
                    }

                    // Genres Row
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        movie.genres.forEach { genre ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF222834))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(genre, fontSize = 11.sp, color = Color.LightGray)
                            }
                        }
                    }

                    // Plot description
                    Text(
                        text = movie.plot,
                        fontSize = 14.sp,
                        color = Color(0xFFC0CAD9),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )

                    // LIVE Torrent Seeder Health Tracker
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color(0xFF13171E), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF2C3545), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "DHT Torrent Seeder Graph",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Health: Optimal",
                                fontSize = 10.sp,
                                color = Color(0xFF2A9D8F),
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(Color(0xFF2A9D8F)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Seeders: ${movie.seedCount}", fontSize = 12.sp, color = Color.White)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(Color(0xFF00B4D8)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Peers: ${movie.peerCount}", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }

                    // --- CHOSE RESOLUTION DOWNLOAD SPEED SELECTOR ---
                    Text(
                        "1. Choose Target Seed Resolution Link",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val resolutions = listOf(
                            "4K UHD Atmos Link",
                            "1080p High Speed Direct",
                            "1080p WebRip DD5.1",
                            "720p Mobile Compact Rip"
                        )
                        resolutions.forEach { resOption ->
                            val isSelected = selectedResolutionOption == resOption
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF202633) else Color(0xFF1B222E))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF2C3545),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedResolutionOption = resOption }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedResolutionOption = resOption },
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(resOption, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Speed: ~84MB/s", fontSize = 10.sp, color = Color(0xFF5A728F))
                                    }
                                }
                            }
                        }
                    }

                    // --- PRIMARY INTERACTION ACTIONS ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Watch Toggle Option
                        Button(
                            onClick = {
                                isStreamingMode = !isStreamingMode
                                isPlayingStream = isStreamingMode
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("stream_action_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isStreamingMode) Color(0xFF222834) else Color(0xFF2A9D8F)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                if (isStreamingMode) Icons.Default.VisibilityOff else Icons.Default.SlowMotionVideo,
                                contentDescription = "Stream playback icons"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isStreamingMode) "Details Banner" else "Watch Stream App",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Start Download simulation in background Database
                        Button(
                            onClick = {
                                viewModel.startSimulatedDownload(movie, selectedResolutionOption)
                                Toast.makeText(context, "Direct peer links decrypted. Added to Downloads!", Toast.LENGTH_LONG).show()
                                viewModel.selectMovie(null) // return to dashboard
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(52.dp)
                                .testTag("download_action_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.DownloadForOffline, contentDescription = "Simulate download tracker")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Generate Link & Download",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // --- SCHEDULER: REQUEST LEAK FORM OVERLAY VIEW ---
        if (showingRequestDialog) {
            AlertDialog(
                onDismissRequest = { showingRequestDialog = false },
                title = { Text("Request Target Movie Leak Scan", color = Color.White) },
                text = {
                    var inputTitle by remember { mutableStateOf("") }
                    var inputIndustry by remember { mutableStateOf("Bollywood") }
                    var inputRes by remember { mutableStateOf("4K UHD HEVC Uncut") }

                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Cannot find a popular new Bollywood or Punjabi release? Submit a direct leak search command to our indexing scrapers. Live status reports display on the dashboard.",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        
                        OutlinedTextField(
                            value = inputTitle,
                            onValueChange = { inputTitle = it },
                            placeholder = { Text("Movie Title (e.g. Singham Again)", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )

                        // Selector chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Bollywood", "Punjabi").forEach { ind ->
                                val isSelected = inputIndustry == ind
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF1B222E))
                                        .clickable { inputIndustry = ind }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(ind, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Submit action
                        Button(
                            onClick = {
                                if (inputTitle.isNotBlank()) {
                                    viewModel.submitLeakRequest(inputTitle, inputIndustry, inputRes)
                                    Toast.makeText(context, "Substituted dynamic script! Scanning global indexes...", Toast.LENGTH_SHORT).show()
                                    showingRequestDialog = false
                                } else {
                                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Deploy Leak Scraper", fontWeight = FontWeight.Black)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showingRequestDialog = false }) {
                        Text("Cancel", color = Color.LightGray)
                    }
                },
                containerColor = Color(0xFF13171E),
                textContentColor = Color.White
            )
        }
    }
}

// --- SUB-SCREEN CONTENT: THE CATALOG WRAPPER ---
@Composable
fun CatalogTabContent(
    movies: List<Movie>,
    onMovieSelected: (Movie) -> Unit,
    onRequestLeakClicked: () -> Unit
) {
    if (movies.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.HourglassEmpty, contentDescription = "Not found", modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No indexing records match your query.",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Deploy custom scrapers or check filter configurations.",
                color = Color.LightGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestLeakClicked,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Request custom Leak Scan")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            
            // Proactive Request Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF20161A)),
                    border = BorderStroke(1.dp, Color(0xFFE50914).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Punjabi & Bollywood Request Line",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                "Submit movie details to generate dynamic P2P seeds instantly.",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                        Button(
                            onClick = onRequestLeakClicked,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Request Leak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Regular items list
            items(movies, key = { it.id }) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMovieSelected(movie) }
                        .testTag("movie_card_${movie.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        // Card Header Hero block simulation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(movie.backdropColorHex),
                                            Color(0xFF1B222E)
                                        )
                                    )
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = movie.statusBadge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE9C46A)
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = "Active Seeds",
                                            modifier = Modifier.size(10.dp),
                                            tint = Color(0xFFE63946)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            "DHT: ${(movie.seedCount / 1000)}k",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Info container
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = movie.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "★ ${movie.rating}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB703)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${movie.industry} • ${movie.duration} • ${movie.rYear}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8A9AAD)
                                )
                                Text(
                                    text = movie.fileSize,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00B4D8)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = movie.plot,
                                fontSize = 12.sp,
                                color = Color(0xFFC0CAD9),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- SUB-SCREEN CONTENT: DOWNLOAD WORKMANAGER ENTITIES ---
@Composable
fun DownloadsTabContent(
    downloads: List<MovieDownloadEntity>,
    onPauseResume: (MovieDownloadEntity) -> Unit,
    onDelete: (String) -> Unit,
    onStreamClicked: (MovieDownloadEntity) -> Unit
) {
    if (downloads.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CloudDownload, contentDescription = "Downloads list empty", modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No direct mirror files are currently downloading.",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Choose a Bollywood or Punjabi movie & click 'Generate Link & Download' to trigger unlimited multi-threaded P2P speeds.",
                color = Color.LightGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(downloads, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("download_card_${item.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B222E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.resolution} • Size: ${item.fileSize}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF8A9AAD)
                                )
                            }
                            
                            // Trash Delete Action
                            IconButton(onClick = { onDelete(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete download task", tint = Color(0xFFE63946))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Indicator Line
                        LinearProgressIndicator(
                            progress = { item.downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (item.status == "COMPLETED") Color(0xFF2A9D8F) else MaterialTheme.colorScheme.primary,
                            trackColor = Color(0xFF13171E)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progress: ${item.downloadProgress}% • ${item.downloadSpeed}",
                                fontSize = 11.sp,
                                color = if (item.status == "COMPLETED") Color(0xFF2A9D8F) else Color(0xFFC0CAD9),
                                fontWeight = FontWeight.SemiBold
                            )

                            // Play Stream or Pause / Resume Controls
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (item.status != "COMPLETED") {
                                    Button(
                                        onClick = { onPauseResume(item) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (item.status == "DOWNLOADING") Color(0xFFE50914) else Color(0xFF2A9D8F)
                                        ),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            text = if (item.status == "DOWNLOADING") "Pause" else "Resume",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    // Play Stream directly
                                    Button(
                                        onClick = { onStreamClicked(item) },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A9D8F)),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play button", modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Play Stream",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
