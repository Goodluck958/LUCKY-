package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.OmniDatabase
import com.example.data.model.*
import com.example.data.repository.OmniRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class OmniTab(val title: String, val platformIconHint: String) {
    PULSE("Pulse", "Shorts/TikTok"),
    STREAM("Stream", "YouTube"),
    LOUNGE("Lounge", "Instagram/FB"),
    ECHOES("Echoes", "X/Twitter"),
    DIRECT("Direct", "WhatsApp")
}

data class OmniUiState(
    val currentTab: OmniTab = OmniTab.PULSE,
    val reels: List<FeedItemEntity> = emptyList(),
    val streams: List<FeedItemEntity> = emptyList(),
    val feeds: List<FeedItemEntity> = emptyList(),
    val sparks: List<FeedItemEntity> = emptyList(),
    val stories: List<StoryEntity> = emptyList(),
    val conversations: List<ChatConversationEntity> = emptyList(),
    val adCampaigns: List<AdCampaignEntity> = emptyList(),
    val activeCampaigns: List<AdCampaignEntity> = emptyList(),
    val algorithmProfile: AlgorithmProfileEntity = AlgorithmProfileEntity(),
    val wallet: UserWalletEntity = UserWalletEntity(),
    val announcements: List<AdminAnnouncementEntity> = emptyList(),
    val reports: List<AdminReportEntity> = emptyList(),
    val securityConfig: SecurityConfig = SecurityConfig(),
    val activeSessions: List<ActiveSessionItem> = listOf(
        ActiveSessionItem("s1", "Google Pixel 9 Pro (Android 15)", "Mobile", "San Francisco, CA", "192.0.2.45", "Active Now", true),
        ActiveSessionItem("s2", "MacBook Pro M3 Max", "Desktop", "San Francisco, CA", "192.0.2.98", "2 hours ago", false),
        ActiveSessionItem("s3", "Ubuntu Cloud Cluster (Admin Node)", "Server", "Frankfurt, DE", "185.12.89.4", "Yesterday", false)
    ),
    val availableGifts: List<GiftItem> = listOf(
        GiftItem("g1", "Lucky Clock ⏰", "⏰", 100, "Good luck & high velocity!", "CLOCK_SPARK"),
        GiftItem("g2", "Viral Rocket 🚀", "🚀", 500, "Boosts post into trending space!", "ROCKET_BOOST"),
        GiftItem("g3", "Diamond Gem 💎", "💎", 1000, "Direct creator appreciation tip!", "DIAMOND_SHINE"),
        GiftItem("g4", "Royal Crown 👑", "👑", 2500, "VIP status gift with golden banner!", "CROWN_RAIN"),
        GiftItem("g5", "Galaxy Portal 🌌", "🌌", 5000, "Ultimate super-fan portal tribute!", "GALAXY_EXPLOSION")
    ),
    val selectedPostComments: List<CommentEntity> = emptyList(),
    val activeCommentPostId: String? = null,
    val selectedConversationId: String? = null,
    val activeChatMessages: List<ChatMessageEntity> = emptyList(),
    val activeStory: StoryEntity? = null,
    val showAlgorithmTunerModal: Boolean = false,
    val showAdManagerModal: Boolean = false,
    val showCreatePostModal: Boolean = false,
    val showAdminPortalModal: Boolean = false,
    val showSettingsSecurityModal: Boolean = false,
    val showMonetizationHubModal: Boolean = false,
    val showGiftSendModal: Boolean = false,
    val giftTargetPost: FeedItemEntity? = null,
    val showCallDialog: Boolean = false,
    val callContactName: String = "",
    val callIsVideo: Boolean = true,
    val selectedStreamVideo: FeedItemEntity? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val toastMessage: String? = null
)

class OmniViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OmniRepository = OmniRepository(OmniDatabase.getInstance(application))

    private val _uiState = MutableStateFlow(OmniUiState())
    val uiState: StateFlow<OmniUiState> = _uiState.asStateFlow()

    init {
        // Collect feed items
        viewModelScope.launch {
            repository.getAllFeedItems().collect { allItems ->
                _uiState.update { state ->
                    val defaultStream = if (state.selectedStreamVideo == null) {
                        allItems.firstOrNull { it.type == FeedType.STREAM }
                    } else state.selectedStreamVideo

                    state.copy(
                        reels = allItems.filter { it.type == FeedType.REEL },
                        streams = allItems.filter { it.type == FeedType.STREAM },
                        feeds = allItems.filter { it.type == FeedType.FEED },
                        sparks = allItems.filter { it.type == FeedType.SPARK },
                        selectedStreamVideo = defaultStream
                    )
                }
            }
        }

        // Collect stories
        viewModelScope.launch {
            repository.getAllStories().collect { stories ->
                _uiState.update { it.copy(stories = stories) }
            }
        }

        // Collect conversations
        viewModelScope.launch {
            repository.getAllConversations().collect { convs ->
                _uiState.update { it.copy(conversations = convs) }
            }
        }

        // Collect ad campaigns
        viewModelScope.launch {
            repository.getAllCampaigns().collect { campaigns ->
                _uiState.update {
                    it.copy(
                        adCampaigns = campaigns,
                        activeCampaigns = campaigns.filter { c -> c.isActive }
                    )
                }
            }
        }

        // Collect algorithm profile
        viewModelScope.launch {
            repository.getAlgorithmProfile().collect { profile ->
                profile?.let { prof ->
                    _uiState.update { it.copy(algorithmProfile = prof) }
                }
            }
        }

        // Collect wallet
        viewModelScope.launch {
            repository.getWallet().collect { wallet ->
                wallet?.let { w ->
                    _uiState.update { it.copy(wallet = w) }
                }
            }
        }

        // Collect admin announcements
        viewModelScope.launch {
            repository.getAllAnnouncements().collect { ann ->
                _uiState.update { it.copy(announcements = ann) }
            }
        }

        // Collect admin reports
        viewModelScope.launch {
            repository.getAllReports().collect { reps ->
                _uiState.update { it.copy(reports = reps) }
            }
        }
    }

    fun selectTab(tab: OmniTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    // Engagement actions
    fun toggleLike(feedItemId: String, currentIsLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLike(feedItemId, currentIsLiked)
        }
    }

    fun toggleBookmark(feedItemId: String, currentIsBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(feedItemId, currentIsBookmarked)
            showToast(if (!currentIsBookmarked) "Saved to Bookmarks" else "Removed from Bookmarks")
        }
    }

    fun toggleRepost(feedItemId: String, currentIsReposted: Boolean) {
        viewModelScope.launch {
            repository.toggleRepost(feedItemId, currentIsReposted)
            showToast(if (!currentIsReposted) "Reposted to your Echoes!" else "Undo Repost")
        }
    }

    fun recordWatch(feedItemId: String, seconds: Int) {
        viewModelScope.launch {
            repository.recordWatchTime(feedItemId, seconds)
        }
    }

    // Stream Selection
    fun selectStreamVideo(item: FeedItemEntity) {
        _uiState.update { it.copy(selectedStreamVideo = item) }
        recordWatch(item.id, 15)
    }

    // Comments
    fun openCommentsForPost(feedItemId: String) {
        _uiState.update { it.copy(activeCommentPostId = feedItemId) }
        viewModelScope.launch {
            repository.getCommentsForPost(feedItemId).collect { comments ->
                _uiState.update { it.copy(selectedPostComments = comments) }
            }
        }
    }

    fun closeComments() {
        _uiState.update { it.copy(activeCommentPostId = null, selectedPostComments = emptyList()) }
    }

    fun postComment(feedItemId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(feedItemId, text)
            showToast("Comment published!")
        }
    }

    fun toggleCommentLike(commentId: String, currentIsLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleCommentLike(commentId, currentIsLiked)
        }
    }

    // Stories
    fun openStory(story: StoryEntity) {
        _uiState.update { it.copy(activeStory = story) }
        viewModelScope.launch {
            repository.markStorySeen(story.id)
        }
    }

    fun closeStory() {
        _uiState.update { it.copy(activeStory = null) }
    }

    // Chats
    fun openConversation(conversationId: String) {
        _uiState.update { it.copy(selectedConversationId = conversationId) }
        viewModelScope.launch {
            repository.markConversationRead(conversationId)
            repository.getMessagesForConversation(conversationId).collect { messages ->
                _uiState.update { it.copy(activeChatMessages = messages) }
            }
        }
    }

    fun closeConversation() {
        _uiState.update { it.copy(selectedConversationId = null, activeChatMessages = emptyList()) }
    }

    fun sendChatMessage(conversationId: String, text: String, isVoice: Boolean = false, durationSec: Int = 0) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(conversationId, text, isVoice, durationSec)
            // Simulate realistic reply after 1.5 seconds if talking to AI or contact
            delay(1500)
            val conv = _uiState.value.conversations.find { it.id == conversationId }
            val autoReply = when {
                conv?.id == "conv-1" -> "⚡ Omni AI: Got your prompt! Optimized your recommendation weights accordingly."
                conv?.id == "conv-2" -> "Thanks! Let me test that in the pipeline now 👍"
                else -> "Awesome, talk to you soon!"
            }
            repository.sendMessage(conversationId, autoReply, false, 0)
        }
    }

    fun startCall(contactName: String, isVideo: Boolean) {
        _uiState.update {
            it.copy(
                showCallDialog = true,
                callContactName = contactName,
                callIsVideo = isVideo
            )
        }
    }

    fun endCall() {
        _uiState.update { it.copy(showCallDialog = false) }
    }

    // Algorithm Tuner
    fun setAlgorithmTunerVisible(visible: Boolean) {
        _uiState.update { it.copy(showAlgorithmTunerModal = visible) }
    }

    fun updateAlgorithmWeights(
        tech: Float,
        gaming: Float,
        humor: Float,
        lifestyle: Float,
        music: Float,
        freshness: Float,
        echoChamberBreaker: Float
    ) {
        val current = _uiState.value.algorithmProfile
        val updated = current.copy(
            techWeight = tech,
            gamingWeight = gaming,
            humorWeight = humor,
            lifestyleWeight = lifestyle,
            musicWeight = music,
            freshnessBias = freshness,
            echoChamberBreaker = echoChamberBreaker
        )
        viewModelScope.launch {
            repository.updateAlgorithmProfile(updated)
            showToast("🧠 Algorithm updated! Feeds re-calibrated.")
        }
    }

    // Ad Studio & Campaigns
    fun setAdManagerVisible(visible: Boolean) {
        _uiState.update { it.copy(showAdManagerModal = visible) }
    }

    fun toggleCampaign(campaignId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleCampaignActive(campaignId, !isActive)
            showToast(if (!isActive) "Campaign Resumed 🚀" else "Campaign Paused ⏸️")
        }
    }

    fun createAdCampaign(
        name: String,
        brand: String,
        objective: AdObjective,
        dailyBudget: Double,
        headline: String,
        body: String,
        ctaText: String,
        ctaUrl: String,
        category: TopicCategory,
        mediaUrl: String
    ) {
        val campaign = AdCampaignEntity(
            id = "ad-" + UUID.randomUUID().toString().take(8),
            campaignName = name,
            brandName = brand,
            objective = objective,
            dailyBudget = dailyBudget,
            totalBudget = dailyBudget * 10,
            spentBudget = 0.0,
            targetCategory = category,
            targetAudienceDescription = "Targeting ${category.name.lowercase()} enthusiasts",
            headline = headline,
            bodyCopy = body,
            ctaText = ctaText,
            ctaUrl = ctaUrl,
            mediaUrl = mediaUrl.ifEmpty { "drawable/ic_ad_banner" },
            isActive = true
        )

        // Also inject into in-feed sponsored items!
        val sponsoredFeedItem = FeedItemEntity(
            id = "sponsored-" + campaign.id,
            type = FeedType.FEED,
            authorName = brand,
            authorHandle = "@" + brand.lowercase().replace(" ", "_"),
            authorAvatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=150",
            authorVerified = true,
            title = headline,
            content = body,
            mediaUrl = campaign.mediaUrl,
            thumbnailUrl = campaign.mediaUrl,
            likesCount = 120,
            isLiked = false,
            commentsCount = 14,
            isSponsored = true,
            sponsorBrand = brand,
            sponsorCtaText = ctaText,
            sponsorCtaUrl = ctaUrl,
            affinityReason = "Sponsored Campaign · ${campaign.campaignName}",
            algorithmScore = 0.85f
        )

        viewModelScope.launch {
            repository.createAdCampaign(campaign)
            repository.createPost(sponsoredFeedItem)
            showToast("🎯 Ad Campaign launched & weaved into feeds!")
        }
    }

    fun onAdClicked(campaignId: String) {
        viewModelScope.launch {
            repository.recordAdClick(campaignId)
            showToast("Ad CTA Triggered (Click Recorded in Ad Analytics)")
        }
    }

    // Post Creation
    fun setCreatePostVisible(visible: Boolean) {
        _uiState.update { it.copy(showCreatePostModal = visible) }
    }

    fun publishPost(
        type: FeedType,
        title: String,
        content: String,
        tags: String,
        category: TopicCategory,
        mediaRes: String
    ) {
        val newPost = FeedItemEntity(
            id = "post-" + UUID.randomUUID().toString().take(8),
            type = type,
            authorName = "Alex Carter",
            authorHandle = "@alex_omni",
            authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
            authorVerified = true,
            title = title,
            content = content,
            tags = tags,
            topicCategory = category,
            mediaUrl = mediaRes.ifEmpty { "drawable/ic_reel_cyber" },
            thumbnailUrl = mediaRes.ifEmpty { "drawable/ic_reel_cyber" },
            soundTitle = if (type == FeedType.REEL) "Original Pulse Sound - @alex_omni" else "",
            soundAuthor = "Alex Carter",
            videoDurationSeconds = if (type == FeedType.REEL) 25 else if (type == FeedType.STREAM) 640 else 0,
            affinityReason = "Just published by you ✨",
            algorithmScore = 0.99f,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.createPost(newPost)
            showToast("Published to ${type.name} feed!")
        }
    }

    // Super Admin Portal
    fun setAdminPortalVisible(visible: Boolean) {
        _uiState.update { it.copy(showAdminPortalModal = visible) }
    }

    fun createAnnouncement(title: String, message: String, type: String) {
        viewModelScope.launch {
            repository.createAnnouncement(title, message, type)
            showToast("📢 Global Admin Announcement Broadcasted!")
        }
    }

    fun toggleAnnouncement(id: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAnnouncementActive(id, !isActive)
            showToast(if (!isActive) "Announcement Activated" else "Announcement Hidden")
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
            showToast("Announcement Deleted")
        }
    }

    fun adminBoostPost(postId: String, multiplier: Float) {
        viewModelScope.launch {
            repository.boostPostScore(postId, multiplier)
            showToast("⚡ Post Algorithmic Velocity Boosted by ${multiplier.toInt()}X!")
        }
    }

    fun adminToggleVerified(handle: String, currentVerified: Boolean) {
        viewModelScope.launch {
            repository.setAuthorVerified(handle, !currentVerified)
            showToast(if (!currentVerified) "👑 Creator Verified Badge Granted!" else "Verified Badge Revoked")
        }
    }

    fun adminDeletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showToast("🔨 Post Moderated & Removed by Super-Admin")
        }
    }

    fun adminActionReport(reportId: String, action: String, targetPostId: String = "") {
        viewModelScope.launch {
            repository.actionReport(reportId, action, targetPostId)
            showToast("🛡️ Moderation Report marked: $action")
        }
    }

    fun setPlatformTakeRate(takeRatePercent: Int) {
        viewModelScope.launch {
            repository.updatePlatformTakeRate(takeRatePercent)
            showToast("Platform commission take-rate set to $takeRatePercent%")
        }
    }

    // Security & Settings
    fun setSettingsSecurityVisible(visible: Boolean) {
        _uiState.update { it.copy(showSettingsSecurityModal = visible) }
    }

    fun updateSecurityConfig(newConfig: SecurityConfig) {
        _uiState.update { it.copy(securityConfig = newConfig) }
        showToast("🔒 Security configuration updated and encrypted.")
    }

    fun revokeSession(sessionId: String) {
        _uiState.update { state ->
            state.copy(activeSessions = state.activeSessions.filter { it.id != sessionId })
        }
        showToast("Device session revoked instantly.")
    }

    fun exportPersonalData() {
        showToast("📦 GDPR Data Archive generated & encrypted. Ready for download.")
    }

    // Monetization Hub & In-App Economy
    fun setMonetizationHubVisible(visible: Boolean) {
        _uiState.update { it.copy(showMonetizationHubModal = visible) }
    }

    fun setGiftSendDialogVisible(visible: Boolean, targetPost: FeedItemEntity? = null) {
        _uiState.update { it.copy(showGiftSendModal = visible, giftTargetPost = targetPost) }
    }

    fun openGiftSendDialog(targetPost: FeedItemEntity) {
        setGiftSendDialogVisible(true, targetPost)
    }

    fun buyCoinPackage(coins: Int, costUsd: Double) {
        viewModelScope.launch {
            repository.addCoins(coins)
            showToast("🪙 Purchased $coins Lucky Time Coins (+\$$costUsd added to platform revenue)!")
        }
    }

    fun sendGift(gift: GiftItem, targetPost: FeedItemEntity) {
        if (_uiState.value.wallet.coinBalance < gift.coinPrice) {
            showToast("⚠️ Not enough Lucky Coins! Top up in the Coin Store.")
            return
        }
        viewModelScope.launch {
            repository.sendGift(gift, targetPost.authorHandle)
            setGiftSendDialogVisible(false)
            showToast("✨ Sent ${gift.name} ${gift.iconEmoji} to ${targetPost.authorName}!")
        }
    }

    fun withdrawCreatorEarnings(amount: Double) {
        if (amount <= 0 || amount > _uiState.value.wallet.creatorEarningsUsd) {
            showToast("⚠️ Invalid payout amount.")
            return
        }
        viewModelScope.launch {
            repository.withdrawEarnings(amount)
            showToast("💸 Payout of \$${String.format("%.2f", amount)} disbursed to Stripe/Bank!")
        }
    }

    fun upgradeSubscriptionTier(tier: SubscriptionTier) {
        viewModelScope.launch {
            repository.updateSubscriptionTier(tier)
            showToast("🎉 Upgraded to ${tier.title}! All perks unlocked.")
        }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
        viewModelScope.launch {
            delay(2500)
            _uiState.update { if (it.toastMessage == msg) it.copy(toastMessage = null) else it }
        }
    }
}
