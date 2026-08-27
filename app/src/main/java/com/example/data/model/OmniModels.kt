package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FeedType {
    REEL,    // TikTok / YouTube Shorts style vertical video
    STREAM,  // YouTube long-form video with player & chapters
    FEED,    // Instagram / Facebook photo, carousel, or status
    SPARK,   // Twitter / X microblogging thought / thread
    STORY    // 24h temporary story
}

enum class TopicCategory {
    TECH,
    GAMING,
    HUMOR,
    MUSIC,
    LIFESTYLE,
    FITNESS,
    CRYPTO,
    SCIENCE,
    NEWS
}

@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @PrimaryKey val id: String,
    val type: FeedType,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val authorVerified: Boolean = false,
    val title: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val thumbnailUrl: String = "",
    val mediaAspect: Float = 1.0f,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val repostsCount: Int = 0,
    val isReposted: Boolean = false,
    val isBookmarked: Boolean = false,
    val viewsCount: Long = 0,
    val tags: String = "", // Comma-separated tags
    val topicCategory: TopicCategory = TopicCategory.TECH,
    val soundTitle: String = "Original Sound",
    val soundAuthor: String = "Omni Audio",
    val videoDurationSeconds: Int = 30,
    val isSponsored: Boolean = false,
    val sponsorBrand: String = "",
    val sponsorCtaText: String = "",
    val sponsorCtaUrl: String = "",
    val affinityReason: String = "Curated for you",
    val algorithmScore: Float = 1.0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val feedItemId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val content: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val timestampFormatted: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val mediaUrl: String,
    val caption: String = "",
    val hasUnseen: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_conversations")
data class ChatConversationEntity(
    @PrimaryKey val id: String,
    val contactName: String,
    val contactHandle: String,
    val contactAvatarUrl: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val isPinned: Boolean = false,
    val isGroup: Boolean = false,
    val groupMembersCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderName: String,
    val isMe: Boolean,
    val messageText: String,
    val mediaUrl: String = "",
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0,
    val status: String = "READ", // SENT, DELIVERED, READ
    val isDisappearing: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = "12:00 PM"
)

enum class AdObjective {
    BRAND_AWARENESS,
    APP_INSTALLS,
    VIDEO_VIEWS,
    WEBSITE_CLICKS
}

@Entity(tableName = "ad_campaigns")
data class AdCampaignEntity(
    @PrimaryKey val id: String,
    val campaignName: String,
    val brandName: String,
    val objective: AdObjective,
    val dailyBudget: Double,
    val totalBudget: Double,
    val spentBudget: Double = 0.0,
    val targetCategory: TopicCategory,
    val targetAudienceDescription: String,
    val headline: String,
    val bodyCopy: String,
    val ctaText: String,
    val ctaUrl: String,
    val mediaUrl: String = "",
    val impressions: Int = 0,
    val clicks: Int = 0,
    val conversions: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SubscriptionTier(
    val title: String,
    val monthlyPrice: Double,
    val badgeIcon: String,
    val perks: List<String>
) {
    FREE("Standard User", 0.0, "👤", listOf("Standard feeds", "Basic SD streaming", "Standard algorithm")),
    LUCKY_GOLD("Lucky Gold ⏰", 9.99, "⏰", listOf("Zero ads across all feeds", "Golden Clock Verified Badge", "1080p 60fps streaming", "2x Discovery Boost")),
    LUCKY_PLATINUM("Lucky Platinum 💎", 29.99, "💎", listOf("All Gold perks", "4K Ultra HDR streaming", "Priority direct message inbox", "5x Algorithmic Velocity", "Custom Clock Themes")),
    LUCKY_BLACK("Lucky Black 👑", 99.99, "👑", listOf("Executive Super-Admin access", "10x Viral Multiplier", "Direct Revenue Share Bonus (+10%)", "24/7 VIP Concierge", "Encrypted Vault Pro"));

    val priceFormatted: String
        get() = if (monthlyPrice == 0.0) "Free" else "\$$monthlyPrice / mo"
}

data class GiftItem(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val coinPrice: Int,
    val description: String,
    val animationType: String
)

@Entity(tableName = "admin_announcements")
data class AdminAnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String = "ALERT", // ALERT, PROMO, UPDATE
    val isActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_reports")
data class AdminReportEntity(
    @PrimaryKey val id: String,
    val targetPostId: String,
    val targetAuthorHandle: String,
    val reporterHandle: String,
    val reason: String,
    val status: String = "PENDING", // PENDING, DISMISSED, ACTIONED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val coinBalance: Int = 3450, // Lucky Time Coins ⏰
    val creatorEarningsUsd: Double = 1840.50, // Monetization Payout balance
    val totalCoinsPurchased: Int = 12000,
    val totalEarningsWithdrawnUsd: Double = 4250.00,
    val currentTier: SubscriptionTier = SubscriptionTier.LUCKY_GOLD,
    val platformTakeRatePercent: Int = 30 // Platform cut (30% standard)
)

data class ActiveSessionItem(
    val id: String,
    val deviceName: String,
    val deviceType: String,
    val location: String,
    val ipAddress: String,
    val lastActive: String,
    val isCurrent: Boolean
) {
    val isCurrentDevice: Boolean get() = isCurrent
}

data class SecurityConfig(
    val e2eEncryptionEnabled: Boolean = true,
    val quantumKeyHash: String = "0x9F4A...7B2E (AES-256-GCM)",
    val biometricLockEnabled: Boolean = false,
    val autoLockDurationSeconds: Int = 60,
    val twoFactorAuthEnabled: Boolean = true,
    val twoFactorOtpCode: String = "849 203",
    val ghostModeEnabled: Boolean = false,
    val readReceiptsEnabled: Boolean = true,
    val contentShieldLevel: String = "BALANCED", // STRICT, BALANCED, UNFILTERED
    val disappearingMessagesHours: Int = 0 // 0 = off, 24, 168
)

@Entity(tableName = "algorithm_profile")
data class AlgorithmProfileEntity(
    @PrimaryKey val id: Int = 1,
    val techWeight: Float = 0.85f,
    val gamingWeight: Float = 0.70f,
    val humorWeight: Float = 0.90f,
    val lifestyleWeight: Float = 0.60f,
    val musicWeight: Float = 0.75f,
    val fitnessWeight: Float = 0.50f,
    val cryptoWeight: Float = 0.40f,
    val scienceWeight: Float = 0.80f,
    val newsWeight: Float = 0.45f,
    val freshnessBias: Float = 0.75f,
    val echoChamberBreaker: Float = 0.35f, // Serendipity factor
    val adDensity: Float = 0.20f, // 1 ad per 5 posts
    val totalWatchTimeMinutes: Int = 142,
    val interactionsCount: Int = 38
)


