package com.example.ui.echoes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedItemEntity
import com.example.ui.OmniViewModel
import com.example.ui.components.*
import com.example.ui.pulse.formatCount
import com.example.ui.theme.*

data class TrendingTopic(val tag: String, val category: String, val sparksPerHour: String, val isHot: Boolean)

@Composable
fun EchoesScreen(
    sparks: List<FeedItemEntity>,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    val trendingTopics = remember {
        listOf(
            TrendingTopic("#QuantumAI", "Tech · Trending", "148K sparks/hr", true),
            TrendingTopic("#MarsSampleReturn", "Science · NASA", "94K sparks/hr", true),
            TrendingTopic("#OmniSuperApp", "Platform", "82K sparks/hr", false),
            TrendingTopic("#RustLang2026", "Developers", "45K sparks/hr", false),
            TrendingTopic("#DeepSpaceTelescope", "Astronomy", "38K sparks/hr", false)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("echoes_lazy_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Trending Topics Velocity Bar
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = TwitterBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Neural Trends",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Real-time Velocity 🔥",
                        color = BrightAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trendingTopics) { topic ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ObsidianSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                            onClick = { /* Filter topic */ }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(topic.category, color = TextMuted, fontSize = 10.sp)
                                Text(topic.tag, color = TwitterBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(topic.sparksPerHour, color = if (topic.isHot) SunsetPink else TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            Divider(color = ObsidianBorder, thickness = 1.dp)
        }

        // 2. Quick Compose Spark Bar
        item {
            var quickText by remember { mutableStateOf("") }
            Surface(
                color = ObsidianSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OmniMediaImage(
                        mediaUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        contentDescription = "Your avatar",
                        modifier = Modifier.size(38.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = quickText,
                        onValueChange = { quickText = it },
                        placeholder = { Text("Post a spark to the neural sphere...", color = TextMuted, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = TwitterBlue.copy(alpha = 0.5f),
                            unfocusedContainerColor = ObsidianSurfaceElevated,
                            focusedContainerColor = ObsidianSurfaceElevated,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (quickText.isNotBlank()) {
                                viewModel.publishPost(
                                    com.example.data.model.FeedType.SPARK,
                                    "",
                                    quickText,
                                    "spark,thoughts",
                                    com.example.data.model.TopicCategory.TECH,
                                    ""
                                )
                                quickText = ""
                            }
                        },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(TwitterBlue)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Divider(color = ObsidianBorder, thickness = 1.dp)
        }

        // 3. Sparks Stream
        items(sparks) { spark ->
            SparkCard(
                spark = spark,
                onLike = { viewModel.toggleLike(spark.id, spark.isLiked) },
                onBookmark = { viewModel.toggleBookmark(spark.id, spark.isBookmarked) },
                onRepost = { viewModel.toggleRepost(spark.id, spark.isReposted) },
                onComments = { viewModel.openCommentsForPost(spark.id) },
                onAlgorithmClick = { viewModel.setAlgorithmTunerVisible(true) },
                onTip = { viewModel.openGiftSendDialog(spark) }
            )
            Divider(color = ObsidianBorder.copy(alpha = 0.4f), thickness = 1.dp)
        }
    }
}

@Composable
fun SparkCard(
    spark: FeedItemEntity,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onRepost: () -> Unit,
    onComments: () -> Unit,
    onAlgorithmClick: () -> Unit,
    onTip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .clickable(onClick = onComments)
            .padding(14.dp)
    ) {
        // Author Avatar
        OmniMediaImage(
            mediaUrl = spark.authorAvatarUrl,
            contentDescription = spark.authorName,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Header Row: Author Name, Handle, Algorithm Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = spark.authorName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (spark.authorVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(size = 12)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = spark.authorHandle,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                AlgorithmBadge(
                    affinityReason = spark.affinityReason,
                    algorithmScore = spark.algorithmScore,
                    onClick = onAlgorithmClick
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body Text
            Text(
                text = spark.content,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Attached Media if available
            if (spark.mediaUrl.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    OmniMediaImage(
                        mediaUrl = spark.mediaUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action Bar (Comment, Repost, Like, Bookmark, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reply
                Row(
                    modifier = Modifier.clickable(onClick = onComments),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(formatCount(spark.commentsCount), color = TextMuted, fontSize = 12.sp)
                }

                // Repost
                Row(
                    modifier = Modifier.clickable(onClick = onRepost),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = null,
                        tint = if (spark.isReposted) NeonGreen else TextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        formatCount(spark.repostsCount),
                        color = if (spark.isReposted) NeonGreen else TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Like
                Row(
                    modifier = Modifier.clickable(onClick = onLike),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (spark.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (spark.isLiked) SunsetPink else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        formatCount(spark.likesCount),
                        color = if (spark.isLiked) SunsetPink else TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Bookmark
                IconButton(onClick = onBookmark, modifier = Modifier.size(24.dp)) {
                    Icon(
                        if (spark.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = if (spark.isBookmarked) BrightAmber else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Tip ⏰
                IconButton(onClick = onTip, modifier = Modifier.size(24.dp)) {
                    Text("⏰", fontSize = 13.sp)
                }
            }
        }
    }
}
