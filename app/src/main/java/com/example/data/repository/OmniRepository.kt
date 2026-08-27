package com.example.data.repository

import com.example.algorithm.AlgorithmEngine
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class OmniRepository(
    private val database: OmniDatabase
) {
    private val feedDao = database.feedDao()
    private val commentDao = database.commentDao()
    private val storyDao = database.storyDao()
    private val chatDao = database.chatDao()
    private val adDao = database.adDao()
    private val algorithmDao = database.algorithmDao()
    private val adminDao = database.adminDao()
    private val walletDao = database.walletDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    // Feeds
    fun getAllFeedItems(): Flow<List<FeedItemEntity>> = feedDao.getAllFeedItems()
    fun getFeedItemsByType(type: FeedType): Flow<List<FeedItemEntity>> = feedDao.getFeedItemsByType(type)
    suspend fun getFeedItemById(id: String) = feedDao.getFeedItemById(id)

    suspend fun toggleLike(feedItemId: String, currentIsLiked: Boolean) {
        val delta = if (currentIsLiked) -1 else 1
        feedDao.toggleLike(feedItemId, !currentIsLiked, delta)
        algorithmDao.recordWatchTimeAndInteractions(0, 1)
    }

    suspend fun toggleBookmark(feedItemId: String, currentIsBookmarked: Boolean) {
        feedDao.toggleBookmark(feedItemId, !currentIsBookmarked)
        algorithmDao.recordWatchTimeAndInteractions(0, 1)
    }

    suspend fun toggleRepost(feedItemId: String, currentIsReposted: Boolean) {
        val delta = if (currentIsReposted) -1 else 1
        feedDao.toggleRepost(feedItemId, !currentIsReposted, delta)
        algorithmDao.recordWatchTimeAndInteractions(0, 2)
    }

    suspend fun recordWatchTime(feedItemId: String, seconds: Int) {
        feedDao.incrementViews(feedItemId)
        algorithmDao.recordWatchTimeAndInteractions(maxOf(1, seconds / 60), 1)
    }

    suspend fun createPost(item: FeedItemEntity) {
        feedDao.insertFeedItem(item)
    }

    // Comments
    fun getCommentsForPost(feedItemId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForFeedItem(feedItemId)
    
    suspend fun addComment(feedItemId: String, text: String, authorName: String = "You", authorHandle: String = "@alex_omni") {
        val comment = CommentEntity(
            id = UUID.randomUUID().toString(),
            feedItemId = feedItemId,
            authorName = authorName,
            authorHandle = authorHandle,
            authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            content = text,
            timestampFormatted = "Just now",
            timestamp = System.currentTimeMillis()
        )
        commentDao.insertComment(comment)
        // Also increment comment count on post
        feedDao.getFeedItemById(feedItemId)?.let { post ->
            feedDao.updateFeedItem(post.copy(commentsCount = post.commentsCount + 1))
        }
        algorithmDao.recordWatchTimeAndInteractions(0, 2)
    }

    suspend fun toggleCommentLike(commentId: String, currentIsLiked: Boolean) {
        val delta = if (currentIsLiked) -1 else 1
        commentDao.toggleCommentLike(commentId, !currentIsLiked, delta)
    }

    // Stories
    fun getAllStories(): Flow<List<StoryEntity>> = storyDao.getAllStories()
    suspend fun markStorySeen(storyId: String) = storyDao.markStorySeen(storyId)

    // Chats
    fun getAllConversations(): Flow<List<ChatConversationEntity>> = chatDao.getAllConversations()
    fun getMessagesForConversation(convId: String): Flow<List<ChatMessageEntity>> = chatDao.getMessagesForConversation(convId)
    
    suspend fun sendMessage(conversationId: String, text: String, isVoiceNote: Boolean = false, voiceDurationSec: Int = 0) {
        val now = System.currentTimeMillis()
        val message = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderName = "You",
            isMe = true,
            messageText = text,
            isVoiceNote = isVoiceNote,
            voiceDurationSec = voiceDurationSec,
            status = "SENT",
            timestamp = now,
            timeFormatted = "Just now"
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(conversationId, text, "Just now", now)
    }

    suspend fun markConversationRead(conversationId: String) {
        chatDao.markConversationRead(conversationId)
    }

    // Ad Campaigns
    fun getAllCampaigns(): Flow<List<AdCampaignEntity>> = adDao.getAllCampaigns()
    fun getActiveCampaigns(): Flow<List<AdCampaignEntity>> = adDao.getActiveCampaigns()
    
    suspend fun createAdCampaign(campaign: AdCampaignEntity) {
        adDao.insertCampaign(campaign)
    }

    suspend fun toggleCampaignActive(campaignId: String, isActive: Boolean) {
        adDao.toggleCampaignActive(campaignId, isActive)
    }

    suspend fun recordAdImpression(campaignId: String) {
        adDao.recordImpression(campaignId, 0.008)
    }

    suspend fun recordAdClick(campaignId: String) {
        adDao.recordClick(campaignId, 0.45)
    }

    suspend fun recordAdConversion(campaignId: String) {
        adDao.recordConversion(campaignId)
    }

    // Algorithm
    fun getAlgorithmProfile(): Flow<AlgorithmProfileEntity?> = algorithmDao.getProfile()
    
    suspend fun updateAlgorithmProfile(profile: AlgorithmProfileEntity) {
        algorithmDao.insertOrUpdateProfile(profile)
        // Re-score feed items based on new profile
        val currentItems = feedDao.getAllFeedItems().firstOrNull() ?: emptyList()
        val rescored = AlgorithmEngine.optimizeFeed(currentItems, profile)
        feedDao.insertFeedItems(rescored)
    }

    // Super Admin Operations
    fun getAllAnnouncements(): Flow<List<AdminAnnouncementEntity>> = adminDao.getAllAnnouncements()

    suspend fun createAnnouncement(title: String, message: String, type: String = "ALERT") {
        val announcement = AdminAnnouncementEntity(
            id = "ann-" + UUID.randomUUID().toString().take(8),
            title = title,
            message = message,
            type = type,
            isActive = true
        )
        adminDao.insertAnnouncement(announcement)
    }

    suspend fun toggleAnnouncementActive(id: String, isActive: Boolean) {
        adminDao.toggleAnnouncementActive(id, isActive)
    }

    suspend fun deleteAnnouncement(id: String) {
        adminDao.deleteAnnouncement(id)
    }

    fun getAllReports(): Flow<List<AdminReportEntity>> = adminDao.getAllReports()

    suspend fun submitReport(postId: String, authorHandle: String, reason: String) {
        val report = AdminReportEntity(
            id = "rep-" + UUID.randomUUID().toString().take(8),
            targetPostId = postId,
            targetAuthorHandle = authorHandle,
            reporterHandle = "@admin_security",
            reason = reason,
            status = "PENDING"
        )
        adminDao.insertReport(report)
    }

    suspend fun actionReport(reportId: String, newStatus: String, targetPostId: String = "") {
        adminDao.updateReportStatus(reportId, newStatus)
        if (newStatus == "ACTIONED" && targetPostId.isNotEmpty()) {
            feedDao.deleteFeedItem(targetPostId)
        }
    }

    suspend fun boostPostScore(postId: String, multiplier: Float) {
        adminDao.boostPostScore(postId, multiplier)
    }

    suspend fun setAuthorVerified(handle: String, verified: Boolean) {
        adminDao.setAuthorVerified(handle, verified)
    }

    suspend fun deletePost(postId: String) {
        feedDao.deleteFeedItem(postId)
    }

    // Wallet & Monetization Engine
    fun getWallet(): Flow<UserWalletEntity?> = walletDao.getWallet()

    suspend fun addCoins(coins: Int) {
        walletDao.addCoins(coins)
    }

    suspend fun deductCoins(coins: Int) {
        walletDao.deductCoins(coins)
    }

    suspend fun sendGift(gift: GiftItem, targetAuthorHandle: String) {
        // Deduct user coins
        walletDao.deductCoins(gift.coinPrice)
        // Convert to creator earnings (70% to creator, 30% platform take-rate)
        val grossUsd = gift.coinPrice * 0.01 // e.g. 100 coins = $1.00
        val wallet = walletDao.getWallet().firstOrNull() ?: UserWalletEntity()
        val creatorCut = (100 - wallet.platformTakeRatePercent) / 100.0
        val creatorEarnings = grossUsd * creatorCut
        walletDao.addCreatorEarnings(creatorEarnings)
    }

    suspend fun withdrawEarnings(amount: Double) {
        walletDao.withdrawEarnings(amount)
    }

    suspend fun updateSubscriptionTier(tier: SubscriptionTier) {
        walletDao.updateTier(tier)
    }

    suspend fun updatePlatformTakeRate(takeRatePercent: Int) {
        walletDao.updateTakeRate(takeRatePercent)
    }

    // Seed Data
    private suspend fun seedInitialDataIfNeeded() {
        val existingProfile = algorithmDao.getProfile().firstOrNull()
        if (existingProfile == null) {
            val initialProfile = AlgorithmProfileEntity(
                id = 1,
                techWeight = 0.90f,
                gamingWeight = 0.75f,
                humorWeight = 0.85f,
                lifestyleWeight = 0.65f,
                musicWeight = 0.80f,
                fitnessWeight = 0.55f,
                cryptoWeight = 0.45f,
                scienceWeight = 0.85f,
                newsWeight = 0.50f,
                freshnessBias = 0.80f,
                echoChamberBreaker = 0.35f,
                adDensity = 0.20f,
                totalWatchTimeMinutes = 320,
                interactionsCount = 64
            )
            algorithmDao.insertOrUpdateProfile(initialProfile)
        }

        val existingPosts = feedDao.getAllFeedItems().firstOrNull()
        if (existingPosts.isNullOrEmpty()) {
            val seedPosts = listOf(
                // 1. REELS (TikTok / Shorts)
                FeedItemEntity(
                    id = "reel-1",
                    type = FeedType.REEL,
                    authorName = "CyberNova",
                    authorHandle = "@cybernova",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    authorVerified = true,
                    title = "Cyberpunk Neo Tokyo in 8K ⚡",
                    content = "Exploring the neon rain alleys of Shinjuku with holographic drone lights! What city should I film next? 🌃 #cyberpunk #tokyo #reels #cinematic",
                    mediaUrl = "drawable/ic_reel_cyber",
                    thumbnailUrl = "drawable/ic_reel_cyber",
                    likesCount = 48200,
                    isLiked = false,
                    commentsCount = 1420,
                    sharesCount = 6200,
                    viewsCount = 192000,
                    tags = "cyberpunk,tokyo,cinematic,future",
                    topicCategory = TopicCategory.TECH,
                    soundTitle = "Cyber City Synthwave - Original",
                    soundAuthor = "NeoBeats Studio",
                    videoDurationSeconds = 24,
                    affinityReason = "94% Match · High Tech Affinity 🔥",
                    algorithmScore = 0.94f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45
                ),
                FeedItemEntity(
                    id = "reel-2",
                    type = FeedType.REEL,
                    authorName = "Quantum Coder",
                    authorHandle = "@dev_devon",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                    authorVerified = true,
                    title = "Building an OS in 60 seconds with Gemini 🚀",
                    content = "Watch AI generate a micro-kernel in Rust live! The future of software engineering is wild 🤯 #coding #developer #ai #tech",
                    mediaUrl = "drawable/ic_stream_thumb",
                    thumbnailUrl = "drawable/ic_stream_thumb",
                    likesCount = 31900,
                    isLiked = true,
                    commentsCount = 890,
                    sharesCount = 4100,
                    viewsCount = 125000,
                    tags = "coding,rust,ai,software",
                    topicCategory = TopicCategory.TECH,
                    soundTitle = "Lo-Fi Beats to Hack To",
                    soundAuthor = "CodeVibes",
                    videoDurationSeconds = 45,
                    affinityReason = "96% Match · Developer Velocity",
                    algorithmScore = 0.96f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120
                ),
                FeedItemEntity(
                    id = "reel-3",
                    type = FeedType.REEL,
                    authorName = "Apex Sound",
                    authorHandle = "@apex_audio",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                    authorVerified = true,
                    title = "Quantum Bass Pro Earbuds - Spatial Audio Reimagined",
                    content = "Experience 360° lossless spatial acoustic drivers with real-time neural noise cancellation. Claim 40% launch discount! #sponsored #audio #tech",
                    mediaUrl = "drawable/ic_ad_banner",
                    thumbnailUrl = "drawable/ic_ad_banner",
                    likesCount = 14300,
                    isLiked = false,
                    commentsCount = 310,
                    sharesCount = 1800,
                    viewsCount = 84000,
                    tags = "sponsored,audio,gadgets",
                    topicCategory = TopicCategory.TECH,
                    soundTitle = "Spatial Sound Test 8D",
                    soundAuthor = "Apex Labs",
                    videoDurationSeconds = 15,
                    isSponsored = true,
                    sponsorBrand = "Apex Audio Global",
                    sponsorCtaText = "Order Now (40% Off)",
                    sponsorCtaUrl = "https://example.com/apex-audio",
                    affinityReason = "Sponsored · Relevant to Tech Gear",
                    algorithmScore = 0.88f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 300
                ),

                // 2. STREAMS (YouTube Long-form Videos)
                FeedItemEntity(
                    id = "stream-1",
                    type = FeedType.STREAM,
                    authorName = "Veritas Universe",
                    authorHandle = "@veritas_universe",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                    authorVerified = true,
                    title = "How Quantum Warp Drives Actually Work: The Physics of 2050",
                    content = "In this deep documentary, we explore the theoretical mechanics of warp metric engineering, negative energy density, and what breakthrough discoveries in quantum mechanics mean for interstellar travel.\n\nChapters:\n0:00 - Introduction & The Alcubierre Metric\n4:12 - Casimir Effect & Negative Energy\n12:30 - Quantum Entanglement Telemetry\n24:15 - Laboratory Prototypes & The Future",
                    mediaUrl = "drawable/ic_stream_thumb",
                    thumbnailUrl = "drawable/ic_stream_thumb",
                    likesCount = 128400,
                    isLiked = true,
                    commentsCount = 4920,
                    sharesCount = 14200,
                    viewsCount = 1850000,
                    tags = "quantum,space,science,documentary",
                    topicCategory = TopicCategory.SCIENCE,
                    videoDurationSeconds = 1845, // ~30 mins
                    affinityReason = "98% Match · In-depth Science Curation 🪐",
                    algorithmScore = 0.98f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 6
                ),
                FeedItemEntity(
                    id = "stream-2",
                    type = FeedType.STREAM,
                    authorName = "Marques Tech World",
                    authorHandle = "@marques_reviews",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150",
                    authorVerified = true,
                    title = "The All-In-One Phone of 2026: The Ultimate Breakdown!",
                    content = "We tested the newest flagship holographic device for 30 straight days. Here is the unvarnished truth about battery life, camera sensors, and AI chipsets.",
                    mediaUrl = "drawable/ic_hero_ad_banner",
                    thumbnailUrl = "drawable/ic_ad_banner",
                    likesCount = 89200,
                    isLiked = false,
                    commentsCount = 3120,
                    sharesCount = 7400,
                    viewsCount = 940000,
                    tags = "tech,gadgets,review,smartphone",
                    topicCategory = TopicCategory.TECH,
                    videoDurationSeconds = 1120,
                    affinityReason = "91% Match · Trending Gadgets 🔥",
                    algorithmScore = 0.91f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 14
                ),

                // 3. LOUNGE (Instagram / Facebook Feeds)
                FeedItemEntity(
                    id = "feed-1",
                    type = FeedType.FEED,
                    authorName = "Elena Rostova",
                    authorHandle = "@elena_travels",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                    authorVerified = true,
                    title = "Golden Hour in the Dolomites 🏔️✨",
                    content = "Woke up at 5 AM to catch the sunrise reflecting over the turquoise alpine glacial lake. There is no filter in the world that can replicate the peace of mountain stillness. Where is your bucket list dream spot? 🌲\n\n📍 Lago di Braies, Italy",
                    mediaUrl = "drawable/ic_feed_photo",
                    thumbnailUrl = "drawable/ic_feed_photo",
                    likesCount = 27400,
                    isLiked = true,
                    commentsCount = 612,
                    sharesCount = 1890,
                    viewsCount = 64000,
                    tags = "travel,dolomites,nature,photography",
                    topicCategory = TopicCategory.LIFESTYLE,
                    affinityReason = "89% Match · High Visual Quality 📸",
                    algorithmScore = 0.89f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 180
                ),
                FeedItemEntity(
                    id = "feed-2",
                    type = FeedType.FEED,
                    authorName = "Apex Audio",
                    authorHandle = "@apex_audio",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                    authorVerified = true,
                    title = "Introducing the Quantum Buds 3 Pro 🎧",
                    content = "Precision acoustic architecture engineered with aerospace-grade graphene drivers. Feel every frequency with studio-master clarity.",
                    mediaUrl = "drawable/ic_ad_banner",
                    thumbnailUrl = "drawable/ic_ad_banner",
                    likesCount = 9800,
                    isLiked = false,
                    commentsCount = 240,
                    sharesCount = 890,
                    viewsCount = 45000,
                    tags = "audio,music,gadgets",
                    topicCategory = TopicCategory.TECH,
                    isSponsored = true,
                    sponsorBrand = "Apex Audio Global",
                    sponsorCtaText = "Shop Collection",
                    sponsorCtaUrl = "https://example.com/shop",
                    affinityReason = "Sponsored · Tech Gear Collection",
                    algorithmScore = 0.82f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 360
                ),

                // 4. SPARKS (Twitter / X Microblogging)
                FeedItemEntity(
                    id = "spark-1",
                    type = FeedType.SPARK,
                    authorName = "Satoshi Nexus",
                    authorHandle = "@satoshi_builds",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    authorVerified = true,
                    title = "",
                    content = "The future of social media isn't 5 different fragmented apps with 5 different algorithms selling your attention to 5 different ad networks.\n\nIt's ONE open neural protocol where YOU control the recommendation weights, own your social graph, and monetize directly. We are building the future.",
                    mediaUrl = "",
                    thumbnailUrl = "",
                    likesCount = 64200,
                    isLiked = true,
                    commentsCount = 3840,
                    repostsCount = 18900,
                    sharesCount = 9200,
                    viewsCount = 420000,
                    tags = "social,openweb,future,decentralized",
                    topicCategory = TopicCategory.TECH,
                    affinityReason = "99% Match · Highest Resonance Score ⚡",
                    algorithmScore = 0.99f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30
                ),
                FeedItemEntity(
                    id = "spark-2",
                    type = FeedType.SPARK,
                    authorName = "Astro Chronicle",
                    authorHandle = "@astro_chronicle",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                    authorVerified = true,
                    title = "",
                    content = "BREAKING: NASA's deep field telescope just spotted atmospheric biosignatures in the habitable zone of Proxima Centauri b. Methane and ozone detected simultaneously in high concentrations. 🌌🔭 This is not a drill.",
                    mediaUrl = "drawable/ic_stream_thumb",
                    thumbnailUrl = "drawable/ic_stream_thumb",
                    likesCount = 98400,
                    isLiked = false,
                    commentsCount = 7620,
                    repostsCount = 34100,
                    sharesCount = 16400,
                    viewsCount = 890000,
                    tags = "breaking,space,nasa,astronomy",
                    topicCategory = TopicCategory.SCIENCE,
                    affinityReason = "97% Match · Breaking Viral Topic 🔥",
                    algorithmScore = 0.97f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 90
                ),
                FeedItemEntity(
                    id = "spark-3",
                    type = FeedType.SPARK,
                    authorName = "Dev Meme Vault",
                    authorHandle = "@git_push_master",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=150",
                    authorVerified = false,
                    title = "",
                    content = "Junior dev: \"It works on my machine!\"\nSenior dev: \"Then we'll ship your machine to the user.\"\nDocker was born. 🐳😂",
                    mediaUrl = "",
                    thumbnailUrl = "",
                    likesCount = 41200,
                    isLiked = true,
                    commentsCount = 1200,
                    repostsCount = 9800,
                    sharesCount = 3200,
                    viewsCount = 280000,
                    tags = "devhumor,docker,programming",
                    topicCategory = TopicCategory.HUMOR,
                    affinityReason = "92% Match · High Humor Affinity 😄",
                    algorithmScore = 0.92f,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 240
                )
            )
            feedDao.insertFeedItems(seedPosts)
        }

        // Stories
        val existingStories = storyDao.getAllStories().firstOrNull()
        if (existingStories.isNullOrEmpty()) {
            val stories = listOf(
                StoryEntity("story-1", "Elena Rostova", "@elena_travels", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150", "drawable/ic_feed_photo", "Sunrise in Alps 🏔️", true),
                StoryEntity("story-2", "CyberNova", "@cybernova", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", "drawable/ic_reel_cyber", "Tokyo Night Vibes ⚡", true),
                StoryEntity("story-3", "Veritas Universe", "@veritas_universe", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", "drawable/ic_stream_thumb", "New deep dive out now! 🪐", true),
                StoryEntity("story-4", "Apex Gear", "@apex_audio", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150", "drawable/ic_ad_banner", "Studio sound test 🎧", false)
            )
            storyDao.insertStories(stories)
        }

        // Chats (WhatsApp style)
        val existingChats = chatDao.getAllConversations().firstOrNull()
        if (existingChats.isNullOrEmpty()) {
            val conversations = listOf(
                ChatConversationEntity(
                    id = "conv-1",
                    contactName = "Omni Core AI & Support",
                    contactHandle = "@omni_ai",
                    contactAvatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
                    lastMessage = "Your algorithm preference profile is syncing smoothly. Ready for test broadcasts!",
                    lastMessageTime = "12:45 PM",
                    unreadCount = 1,
                    isOnline = true,
                    isVerified = true,
                    isPinned = true
                ),
                ChatConversationEntity(
                    id = "conv-2",
                    contactName = "Sarah Chen (VFX Designer)",
                    contactHandle = "@sarah_vfx",
                    contactAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                    lastMessage = "Did you see the new render on Pulse? The neon lighting looks surreal! 🚀",
                    lastMessageTime = "11:20 AM",
                    unreadCount = 2,
                    isOnline = true,
                    isVerified = true,
                    isPinned = true
                ),
                ChatConversationEntity(
                    id = "conv-3",
                    contactName = "Silicon Valley Tech Syndicate",
                    contactHandle = "@sv_syndicate",
                    contactAvatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=150",
                    lastMessage = "Alex: New quantum chip benchmarks dropped. 10x compute jump.",
                    lastMessageTime = "Yesterday",
                    unreadCount = 0,
                    isOnline = false,
                    isVerified = true,
                    isPinned = false,
                    isGroup = true,
                    groupMembersCount = 1420
                ),
                ChatConversationEntity(
                    id = "conv-4",
                    contactName = "Marcus Brody",
                    contactHandle = "@marcus_fit",
                    contactAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                    lastMessage = "🎤 Voice Message (0:18)",
                    lastMessageTime = "Yesterday",
                    unreadCount = 0,
                    isOnline = false,
                    isVerified = false,
                    isPinned = false
                )
            )
            chatDao.insertConversations(conversations)

            // Seed messages for conv-1 and conv-2
            val messagesConv1 = listOf(
                ChatMessageEntity("m1", "conv-1", "Omni Core AI", false, "Welcome to Omni, your unified universe for Videos, Feeds, Sparks, and Encrypted Messaging! 🌐", "", false, 0, "READ", false, System.currentTimeMillis() - 1000 * 60 * 30, "12:15 PM"),
                ChatMessageEntity("m2", "conv-1", "Omni Core AI", false, "You can test modifying the Algorithm Weights in real time or launch an ad campaign in Ad Studio.", "", false, 0, "READ", false, System.currentTimeMillis() - 1000 * 60 * 20, "12:25 PM"),
                ChatMessageEntity("m3", "conv-1", "Omni Core AI", false, "Your algorithm preference profile is syncing smoothly. Ready for test broadcasts!", "", false, 0, "DELIVERED", false, System.currentTimeMillis() - 1000 * 60 * 5, "12:45 PM")
            )
            val messagesConv2 = listOf(
                ChatMessageEntity("m4", "conv-2", "Sarah Chen", false, "Hey! I just exported that 4K cyberpunk animation we were talking about.", "", false, 0, "READ", false, System.currentTimeMillis() - 1000 * 60 * 60, "10:45 AM"),
                ChatMessageEntity("m5", "conv-2", "You", true, "Awesome, drop it on the Pulse feed so we can test the engagement velocity!", "", false, 0, "READ", false, System.currentTimeMillis() - 1000 * 60 * 50, "10:52 AM"),
                ChatMessageEntity("m6", "conv-2", "Sarah Chen", false, "Did you see the new render on Pulse? The neon lighting looks surreal! 🚀", "", false, 0, "DELIVERED", false, System.currentTimeMillis() - 1000 * 60 * 25, "11:20 AM")
            )
            chatDao.insertMessages(messagesConv1 + messagesConv2)
        }

        // Seed Ad Campaigns
        val existingCampaigns = adDao.getAllCampaigns().firstOrNull()
        if (existingCampaigns.isNullOrEmpty()) {
            val campaigns = listOf(
                AdCampaignEntity(
                    id = "ad-camp-1",
                    campaignName = "Quantum Buds 3 Pro Global Launch",
                    brandName = "Apex Audio Global",
                    objective = AdObjective.APP_INSTALLS,
                    dailyBudget = 150.0,
                    totalBudget = 1500.0,
                    spentBudget = 428.50,
                    targetCategory = TopicCategory.TECH,
                    targetAudienceDescription = "Tech enthusiasts, Audiophiles, Gamers (18-45)",
                    headline = "Quantum Bass Pro Earbuds - Spatial Sound Reimagined",
                    bodyCopy = "Experience 360° lossless spatial acoustic drivers with real-time neural noise cancellation. Claim 40% launch discount!",
                    ctaText = "Order Now (40% Off)",
                    ctaUrl = "https://example.com/apex-audio",
                    mediaUrl = "drawable/ic_ad_banner",
                    impressions = 28450,
                    clicks = 1840,
                    conversions = 312,
                    isActive = true,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 48
                ),
                AdCampaignEntity(
                    id = "ad-camp-2",
                    campaignName = "CyberNova VR Game Pre-Registration",
                    brandName = "Nova Interactive",
                    objective = AdObjective.BRAND_AWARENESS,
                    dailyBudget = 75.0,
                    totalBudget = 800.0,
                    spentBudget = 192.00,
                    targetCategory = TopicCategory.GAMING,
                    targetAudienceDescription = "VR Gamers, Sci-Fi enthusiasts",
                    headline = "Step Into Neo-Tokyo 2088 in Full VR",
                    bodyCopy = "Pre-register now to receive exclusive cybernetic weapon skins on Day 1.",
                    ctaText = "Pre-Register Free",
                    ctaUrl = "https://example.com/cybernova",
                    mediaUrl = "drawable/ic_reel_cyber",
                    impressions = 14200,
                    clicks = 980,
                    conversions = 145,
                    isActive = true,
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24
                )
            )
            adDao.insertCampaigns(campaigns)
        }

        // Seed sample comments
        val existingComments = commentDao.getCommentsForFeedItem("reel-1").firstOrNull()
        if (existingComments.isNullOrEmpty()) {
            val comments = listOf(
                CommentEntity("c1", "reel-1", "Maya Lin", "@maya_visuals", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150", "The lighting in this Shinjuku alley is unmatched! What camera did you use?", 142, false, "2h ago"),
                CommentEntity("c2", "reel-1", "Devon Park", "@dev_devon", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", "The rain reflections make this feel like Blade Runner 2049 🌧️🔥", 89, true, "1h ago"),
                CommentEntity("c3", "stream-1", "Dr. Alan Grant", "@alan_physics", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150", "Chapter 12:30 explaining the negative energy Casimir cavity was brilliant. Best explanation on YouTube/Omni!", 412, true, "3h ago")
            )
            commentDao.insertComments(comments)
        }

        // Seed initial wallet
        val existingWallet = walletDao.getWallet().firstOrNull()
        if (existingWallet == null) {
            val initialWallet = UserWalletEntity(
                id = 1,
                coinBalance = 4850,
                creatorEarningsUsd = 2140.75,
                totalCoinsPurchased = 18500,
                totalEarningsWithdrawnUsd = 6200.00,
                currentTier = SubscriptionTier.LUCKY_GOLD,
                platformTakeRatePercent = 30
            )
            walletDao.insertOrUpdateWallet(initialWallet)
        }

        // Seed announcements
        val existingAnnouncements = adminDao.getAllAnnouncements().firstOrNull()
        if (existingAnnouncements.isNullOrEmpty()) {
            val announcements = listOf(
                AdminAnnouncementEntity(
                    id = "ann-1",
                    title = "⏰ Welcome to Lucky Super Network!",
                    message = "Experience high-speed video streams, quantum-encrypted direct chats, zero-ad VIP tiers, and real-time creator coin tipping.",
                    type = "ALERT",
                    isActive = true,
                    timestamp = System.currentTimeMillis()
                ),
                AdminAnnouncementEntity(
                    id = "ann-2",
                    title = "🔥 Weekend Creator Rewards 2X Multiplier",
                    message = "All live gifts and video tip payouts have zero platform fee this weekend. Keep 100% of your earnings!",
                    type = "PROMO",
                    isActive = true,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 4
                )
            )
            announcements.forEach { adminDao.insertAnnouncement(it) }
        }

        // Seed reports
        val existingReports = adminDao.getAllReports().firstOrNull()
        if (existingReports.isNullOrEmpty()) {
            val reports = listOf(
                AdminReportEntity(
                    id = "rep-1",
                    targetPostId = "reel-3",
                    targetAuthorHandle = "@apex_audio",
                    reporterHandle = "@user_critic99",
                    reason = "Sponsored link check / audio frequency claim verification",
                    status = "PENDING",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120
                )
            )
            reports.forEach { adminDao.insertReport(it) }
        }
    }
}
