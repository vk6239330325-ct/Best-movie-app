package com.example.data.model

data class Movie(
    val id: String,
    val title: String,
    val rYear: String,
    val rating: Double,
    val duration: String,
    val industry: String, // "Bollywood" or "Punjabi"
    val genres: List<String>,
    val plot: String,
    val resolution: String, // e.g., "4K UHD Atmos", "1080p WebRip"
    val fileSize: String,   // e.g., "3.2 GB", "1.4 GB"
    val seedCount: Int,     // Peer-to-peer trackers (seeder)
    val peerCount: Int,     // Peer-to-peer trackers (leecher)
    val ratingVotes: String,
    val statusBadge: String, // e.g., "PRE-RELEASE LEAK", "HOT TREND", "4K HEVC RAW"
    val videoUrl: String,    // Public domain video loops for simulator stream
    val backdropColorHex: Long = 0xFFFF3366 // colorful design cards color fallback
) {
    companion object {
        val curatedCatalog = listOf(
            // --- BOLLYWOOD CLASS ---
            Movie(
                id = "stree_2",
                title = "Stree 2: The Terror Returns",
                rYear = "2024",
                rating = 8.5,
                duration = "147 min",
                industry = "Bollywood",
                genres = listOf("Comedy", "Horror", "Mystery"),
                plot = "Chanderi is haunted once more, but this time by a headless entity known as 'Sarkata'. The town's resident tailor Vicky and his quirky team must team up with the mysterious unnamed guardian Stree to tackle this terrifying threat with loads of humor.",
                resolution = "4K HEVC Atmos Clean Audio",
                fileSize = "4.2 GB",
                seedCount = 148902,
                peerCount = 82431,
                ratingVotes = "120K",
                statusBadge = "HOT TREND • LEAKED",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                backdropColorHex = 0xFFE63946
            ),
            Movie(
                id = "jawan_action",
                title = "Jawan: The Vigilante",
                rYear = "2023",
                rating = 8.2,
                duration = "169 min",
                industry = "Bollywood",
                genres = listOf("Action", "Thriller", "Masala"),
                plot = "A high-octane emotional action thriller about a man who is set out to rectify the wrongs in society. Driven by a personal vendetta while keeping a promise made years ago, he confronts a monstrous outlaw who has caused extreme suffering to many.",
                resolution = "1080p WebRip DD+5.1",
                fileSize = "2.8 GB",
                seedCount = 95210,
                peerCount = 43105,
                ratingVotes = "310K",
                statusBadge = "ULTRA HD DIRECT LINK",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                backdropColorHex = 0xFF1D3557
            ),
            Movie(
                id = "animal_dark",
                title = "Animal: Brutal Devotion",
                rYear = "2023",
                rating = 7.6,
                duration = "201 min",
                industry = "Bollywood",
                genres = listOf("Action", "Drama", "Crime"),
                plot = "A son's obsessive love for his father runs so deep that it manifests as extreme violence when an assassination attempt is made on his father's life. He embarks on a brutal, blood-slicked crusade of absolute vengeance against a rival clan.",
                resolution = "4K UHD HDR 10-Bit",
                fileSize = "7.5 GB",
                seedCount = 84900,
                peerCount = 61200,
                ratingVotes = "180K",
                statusBadge = "RAW DECRYPTED 10-BIT",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                backdropColorHex = 0xFFE07A5F
            ),
            Movie(
                id = "pathaan_spy",
                title = "Pathaan: The Spy Alliance",
                rYear = "2023",
                rating = 7.9,
                duration = "146 min",
                industry = "Bollywood",
                genres = listOf("Action", "Adventure", "Thriller"),
                plot = "An exiled Indian RAW agent, Pathaan, must team up with Rubai, an ex-ISI agent, to take down Jim, a rogue former Indian agent who leads a private terrorist organization planning a devastating biological strike against India.",
                resolution = "1080p BluRay Rip Studio-Mix",
                fileSize = "2.2 GB",
                seedCount = 112000,
                peerCount = 54000,
                ratingVotes = "240K",
                statusBadge = "FAST MULTI-THREAD CDN",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                backdropColorHex = 0xFF3D5A80
            ),
            Movie(
                id = "gadar_2",
                title = "Gadar 2: The Katha Continues",
                rYear = "2023",
                rating = 8.0,
                duration = "170 min",
                industry = "Bollywood",
                genres = listOf("Action", "Drama", "Patriotic"),
                plot = "Set during the Indo-Pakistani War of 1971, Tara Singh journeys back into Pakistan to rescue his beloved son Charanjeet (Jeete), who is being tortured and held captive by Pakistan's brutal military regime under General Hamid Iqbal.",
                resolution = "1080p HDR WebRip",
                fileSize = "3.1 GB",
                seedCount = 67200,
                peerCount = 28941,
                ratingVotes = "95K",
                statusBadge = "DIRECT DOWNLOAD ACTIVE",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                backdropColorHex = 0xFFD62828
            ),

            // --- PUNJABI CLASS ---
            Movie(
                id = "carry_on_jatta_3",
                title = "Carry on Jatta 3",
                rYear = "2023",
                rating = 8.6,
                duration = "135 min",
                industry = "Punjabi",
                genres = listOf("Comedy", "Romance"),
                plot = "When Jass is determined to marry Meet, her high-handed, conservative father rejects the alliance. The situation spirals out of control as Jass fabricates hilarious untruths and switches family houses to impress Meet's dad, causing chaos.",
                resolution = "1080p High Quality WebRip",
                fileSize = "1.8 GB",
                seedCount = 88404,
                peerCount = 31206,
                ratingVotes = "45K",
                statusBadge = "1080P WEB-DL LEAK",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                backdropColorHex = 0xFF2A9D8F
            ),
            Movie(
                id = "jatt_juliet_3",
                title = "Jatt & Juliet 3",
                rYear = "2024",
                rating = 8.8,
                duration = "142 min",
                industry = "Punjabi",
                genres = listOf("Comedy", "Romance", "Drama"),
                plot = "The legendary duo Fateh and Pooja return! As Punjab police constables sent on a top-secret assignment under wraps to standard European shores, their hilarious banter, cultural shockwaves, and underlying love create a wild cinematic ride.",
                resolution = "4K UHD WebRip Native",
                fileSize = "3.8 GB",
                seedCount = 135400,
                peerCount = 76003,
                ratingVotes = "56K",
                statusBadge = "EXCLUSIVE DIGITAL UNLOCKED",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                backdropColorHex = 0xFFE9C46A
            ),
            Movie(
                id = "mastaney_hist",
                title = "Mastaney: Rise of the Warriors",
                rYear = "2023",
                rating = 8.4,
                duration = "150 min",
                industry = "Punjabi",
                genres = listOf("Action", "History", "Drama"),
                plot = "Set in 1739, Nadir Shah's undefeated empire is challenged by fearless Sikh warriors who lay heavy ambush. Nadir demands five ordinary actors pretend to be Sikh fighters to understand their strategies, leading to self-realization of the warriors' true valor.",
                resolution = "4K HEVC Dolby Sound",
                fileSize = "5.5 GB",
                seedCount = 42100,
                peerCount = 18900,
                ratingVotes = "32K",
                statusBadge = "VIP PRE-RELEASE COMPLETE",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                backdropColorHex = 0xFFF4A261
            ),
            Movie(
                id = "warning_2_action",
                title = "Warning 2: The Prison Break",
                rYear = "2024",
                rating = 8.1,
                duration = "138 min",
                industry = "Punjabi",
                genres = listOf("Action", "Thriller", "Crime"),
                plot = "Pamma's legendary quest for dominance is reignited inside the high-security walls of a dark central jail. He schemes an extreme prison escape under the nose of Inspector Kuldeep Singh while keeping lethal rival gangsters at bay in a game of blood.",
                resolution = "1080p Web-DL Audio Cleaned",
                fileSize = "2.1 GB",
                seedCount = 51200,
                peerCount = 22401,
                ratingVotes = "24K",
                statusBadge = "CDN HYBRID LINK",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                backdropColorHex = 0xFFE76F51
            ),
            Movie(
                id = "saunkan_saunkne",
                title = "Saunkan Saunkne",
                rYear = "2022",
                rating = 8.0,
                duration = "132 min",
                industry = "Punjabi",
                genres = listOf("Comedy", "Family", "Drama"),
                plot = "After eight years of marriage, Nirmal and Naseeb are childless. Naseeb urges Nirmal to marry her sister, Kirban, to obtain an heir. However, the sharing of the household between two sisters leads to ultimate hilarity and domestic comic warfare.",
                resolution = "1080p BluRay Rip Multi",
                fileSize = "1.5 GB",
                seedCount = 38040,
                peerCount = 11403,
                ratingVotes = "18K",
                statusBadge = "UNLIMITED ZIP DOWNLOAD",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                backdropColorHex = 0xFF8338EC
            )
        )
    }
}
