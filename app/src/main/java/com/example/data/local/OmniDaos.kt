package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_items ORDER BY algorithmScore DESC, timestamp DESC")
    fun getAllFeedItems(): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items WHERE type = :type ORDER BY algorithmScore DESC, timestamp DESC")
    fun getFeedItemsByType(type: FeedType): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items WHERE id = :id")
    suspend fun getFeedItemById(id: String): FeedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<FeedItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItem(item: FeedItemEntity)

    @Update
    suspend fun updateFeedItem(item: FeedItemEntity)

    @Query("UPDATE feed_items SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun toggleLike(id: String, isLiked: Boolean, delta: Int)

    @Query("UPDATE feed_items SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: String, isBookmarked: Boolean)

    @Query("UPDATE feed_items SET isReposted = :isReposted, repostsCount = repostsCount + :delta WHERE id = :id")
    suspend fun toggleRepost(id: String, isReposted: Boolean, delta: Int)

    @Query("UPDATE feed_items SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Query("UPDATE feed_items SET algorithmScore = :score, affinityReason = :reason WHERE id = :id")
    suspend fun updateAlgorithmScore(id: String, score: Float, reason: String)

    @Query("DELETE FROM feed_items WHERE id = :id")
    suspend fun deleteFeedItem(id: String)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE feedItemId = :feedItemId ORDER BY timestamp DESC")
    fun getCommentsForFeedItem(feedItemId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun toggleCommentLike(id: String, isLiked: Boolean, delta: Int)
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY hasUnseen DESC, timestamp DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET hasUnseen = 0 WHERE id = :id")
    suspend fun markStorySeen(id: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_conversations ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ChatConversationEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ChatConversationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_conversations SET lastMessage = :lastMessage, lastMessageTime = :time, updatedAt = :timestamp WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId: String, lastMessage: String, time: String, timestamp: Long)

    @Query("UPDATE chat_conversations SET unreadCount = 0 WHERE id = :conversationId")
    suspend fun markConversationRead(conversationId: String)
}

@Dao
interface AdDao {
    @Query("SELECT * FROM ad_campaigns ORDER BY createdAt DESC")
    fun getAllCampaigns(): Flow<List<AdCampaignEntity>>

    @Query("SELECT * FROM ad_campaigns WHERE isActive = 1")
    fun getActiveCampaigns(): Flow<List<AdCampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: AdCampaignEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(campaigns: List<AdCampaignEntity>)

    @Update
    suspend fun updateCampaign(campaign: AdCampaignEntity)

    @Query("UPDATE ad_campaigns SET isActive = :isActive WHERE id = :id")
    suspend fun toggleCampaignActive(id: String, isActive: Boolean)

    @Query("UPDATE ad_campaigns SET impressions = impressions + 1, spentBudget = spentBudget + :cost WHERE id = :id")
    suspend fun recordImpression(id: String, cost: Double = 0.005)

    @Query("UPDATE ad_campaigns SET clicks = clicks + 1, spentBudget = spentBudget + :cost WHERE id = :id")
    suspend fun recordClick(id: String, cost: Double = 0.35)

    @Query("UPDATE ad_campaigns SET conversions = conversions + 1 WHERE id = :id")
    suspend fun recordConversion(id: String)
}

@Dao
interface AdminDao {
    @Query("SELECT * FROM admin_announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<AdminAnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AdminAnnouncementEntity)

    @Query("UPDATE admin_announcements SET isActive = :isActive WHERE id = :id")
    suspend fun toggleAnnouncementActive(id: String, isActive: Boolean)

    @Query("DELETE FROM admin_announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)

    @Query("SELECT * FROM admin_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<AdminReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: AdminReportEntity)

    @Query("UPDATE admin_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: String)

    @Query("UPDATE feed_items SET algorithmScore = algorithmScore * :multiplier WHERE id = :id")
    suspend fun boostPostScore(id: String, multiplier: Float)

    @Query("UPDATE feed_items SET authorVerified = :verified WHERE authorHandle = :handle")
    suspend fun setAuthorVerified(handle: String, verified: Boolean)
}

@Dao
interface AlgorithmDao {
    @Query("SELECT * FROM algorithm_profile WHERE id = 1")
    fun getProfile(): Flow<AlgorithmProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: AlgorithmProfileEntity)

    @Query("UPDATE algorithm_profile SET totalWatchTimeMinutes = totalWatchTimeMinutes + :minutes, interactionsCount = interactionsCount + :interactions WHERE id = 1")
    suspend fun recordWatchTimeAndInteractions(minutes: Int, interactions: Int)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1")
    fun getWallet(): Flow<UserWalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: UserWalletEntity)

    @Query("UPDATE user_wallet SET coinBalance = coinBalance + :coins, totalCoinsPurchased = totalCoinsPurchased + :coins WHERE id = 1")
    suspend fun addCoins(coins: Int)

    @Query("UPDATE user_wallet SET coinBalance = coinBalance - :coins WHERE id = 1")
    suspend fun deductCoins(coins: Int)

    @Query("UPDATE user_wallet SET creatorEarningsUsd = creatorEarningsUsd + :amount WHERE id = 1")
    suspend fun addCreatorEarnings(amount: Double)

    @Query("UPDATE user_wallet SET creatorEarningsUsd = 0.0, totalEarningsWithdrawnUsd = totalEarningsWithdrawnUsd + :amount WHERE id = 1")
    suspend fun withdrawEarnings(amount: Double)

    @Query("UPDATE user_wallet SET currentTier = :tier WHERE id = 1")
    suspend fun updateTier(tier: SubscriptionTier)

    @Query("UPDATE user_wallet SET platformTakeRatePercent = :takeRate WHERE id = 1")
    suspend fun updateTakeRate(takeRate: Int)
}

