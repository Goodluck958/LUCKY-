package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.*

class OmniTypeConverters {
    @TypeConverter
    fun fromFeedType(value: FeedType): String = value.name

    @TypeConverter
    fun toFeedType(value: String): FeedType = try {
        FeedType.valueOf(value)
    } catch (e: Exception) {
        FeedType.FEED
    }

    @TypeConverter
    fun fromTopicCategory(value: TopicCategory): String = value.name

    @TypeConverter
    fun toTopicCategory(value: String): TopicCategory = try {
        TopicCategory.valueOf(value)
    } catch (e: Exception) {
        TopicCategory.TECH
    }

    @TypeConverter
    fun fromAdObjective(value: AdObjective): String = value.name

    @TypeConverter
    fun toAdObjective(value: String): AdObjective = try {
        AdObjective.valueOf(value)
    } catch (e: Exception) {
        AdObjective.BRAND_AWARENESS
    }

    @TypeConverter
    fun fromSubscriptionTier(value: SubscriptionTier): String = value.name

    @TypeConverter
    fun toSubscriptionTier(value: String): SubscriptionTier = try {
        SubscriptionTier.valueOf(value)
    } catch (e: Exception) {
        SubscriptionTier.FREE
    }
}

@Database(
    entities = [
        FeedItemEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        ChatConversationEntity::class,
        ChatMessageEntity::class,
        AdCampaignEntity::class,
        AlgorithmProfileEntity::class,
        AdminAnnouncementEntity::class,
        AdminReportEntity::class,
        UserWalletEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(OmniTypeConverters::class)
abstract class OmniDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun commentDao(): CommentDao
    abstract fun storyDao(): StoryDao
    abstract fun chatDao(): ChatDao
    abstract fun adDao(): AdDao
    abstract fun algorithmDao(): AlgorithmDao
    abstract fun adminDao(): AdminDao
    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile
        private var INSTANCE: OmniDatabase? = null

        fun getInstance(context: Context): OmniDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OmniDatabase::class.java,
                    "lucky_superapp.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
