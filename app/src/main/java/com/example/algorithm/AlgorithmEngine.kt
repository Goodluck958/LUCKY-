package com.example.algorithm

import com.example.data.model.AlgorithmProfileEntity
import com.example.data.model.FeedItemEntity
import com.example.data.model.TopicCategory
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object AlgorithmEngine {

    data class ScoringResult(
        val finalScore: Float,
        val matchPercentage: Int,
        val topFactor: String,
        val detailedExplanation: String
    )

    fun scorePost(
        post: FeedItemEntity,
        profile: AlgorithmProfileEntity
    ): ScoringResult {
        // Category weight
        val categoryWeight = when (post.topicCategory) {
            TopicCategory.TECH -> profile.techWeight
            TopicCategory.GAMING -> profile.gamingWeight
            TopicCategory.HUMOR -> profile.humorWeight
            TopicCategory.MUSIC -> profile.musicWeight
            TopicCategory.LIFESTYLE -> profile.lifestyleWeight
            TopicCategory.FITNESS -> profile.fitnessWeight
            TopicCategory.CRYPTO -> profile.cryptoWeight
            TopicCategory.SCIENCE -> profile.scienceWeight
            TopicCategory.NEWS -> profile.newsWeight
        }

        // Engagement signals
        val engagementSignal = min(1.0f, (post.likesCount * 2 + post.commentsCount * 5 + post.sharesCount * 8 + post.repostsCount * 4) / 5000f)
        
        // Freshness decay (within 48 hours)
        val hoursOld = max(0.5f, (System.currentTimeMillis() - post.timestamp) / (1000f * 60f * 60f))
        val freshnessScore = (1.0f / (1.0f + (hoursOld * 0.05f))) * profile.freshnessBias

        // Serendipity / Echo Chamber Breaker factor
        val pseudoRandom = (post.id.hashCode() % 100).let { if (it < 0) it + 100 else it } / 100f
        val serendipityScore = pseudoRandom * profile.echoChamberBreaker

        // Calculate blended score
        val baseScore = (categoryWeight * 0.45f) + (engagementSignal * 0.25f) + (freshnessScore * 0.20f) + (serendipityScore * 0.10f)
        val finalScore = max(0.1f, min(1.0f, baseScore))
        val matchPercentage = (finalScore * 100).toInt()

        val topFactor = when {
            categoryWeight >= 0.8f -> "High ${post.topicCategory.name.lowercase().replaceFirstChar { it.uppercase() }} Affinity"
            engagementSignal >= 0.6f -> "High Viral Velocity 🔥"
            serendipityScore >= 0.25f -> "Serendipitous Discovery 🌟"
            else -> "Recent Fresh Drop ⚡"
        }

        val detailedExplanation = buildString {
            append("$matchPercentage% Match · ")
            append(topFactor)
            if (post.likesCount > 500) {
                append(" (${post.likesCount} community likes)")
            }
        }

        return ScoringResult(
            finalScore = finalScore,
            matchPercentage = matchPercentage,
            topFactor = topFactor,
            detailedExplanation = detailedExplanation
        )
    }

    fun optimizeFeed(
        items: List<FeedItemEntity>,
        profile: AlgorithmProfileEntity
    ): List<FeedItemEntity> {
        return items.map { item ->
            val result = scorePost(item, profile)
            item.copy(
                algorithmScore = result.finalScore,
                affinityReason = result.detailedExplanation
            )
        }.sortedByDescending { it.algorithmScore }
    }
}
