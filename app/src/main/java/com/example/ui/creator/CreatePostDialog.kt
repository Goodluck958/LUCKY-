package com.example.ui.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FeedType
import com.example.data.model.TopicCategory
import com.example.ui.OmniViewModel
import com.example.ui.components.OmniMediaImage
import com.example.ui.theme.*

@Composable
fun CreatePostDialog(
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(FeedType.REEL) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("trending,viral,future") }
    var selectedCategory by remember { mutableStateOf(TopicCategory.TECH) }
    var selectedMediaRes by remember { mutableStateOf("drawable/ic_reel_cyber") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .testTag("create_post_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Create & Broadcast", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Format Selector Pills (Reel, Stream, Photo Feed, Spark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormatSelectorPill("⚡ Pulse Reel", selectedType == FeedType.REEL, TikTokRed) {
                        selectedType = FeedType.REEL
                        selectedMediaRes = "drawable/ic_reel_cyber"
                    }
                    FormatSelectorPill("📺 Stream", selectedType == FeedType.STREAM, YouTubeRed) {
                        selectedType = FeedType.STREAM
                        selectedMediaRes = "drawable/ic_stream_thumb"
                    }
                    FormatSelectorPill("📷 Lounge", selectedType == FeedType.FEED, InstagramGradient1) {
                        selectedType = FeedType.FEED
                        selectedMediaRes = "drawable/ic_feed_photo"
                    }
                    FormatSelectorPill("💬 Spark", selectedType == FeedType.SPARK, TwitterBlue) {
                        selectedType = FeedType.SPARK
                        selectedMediaRes = ""
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedType == FeedType.STREAM || selectedType == FeedType.REEL) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Video Title") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                    }

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Caption / Content / Description") },
                        placeholder = { Text("Write something engaging...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags & Keywords (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )

                    // Media Thumbnail Preview
                    if (selectedMediaRes.isNotEmpty()) {
                        Text("Attached Media Asset:", color = TextSecondary, fontSize = 12.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            OmniMediaImage(
                                mediaUrl = selectedMediaRes,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Algorithm Optimization Gauge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ObsidianSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Predicted Algorithm Reach: 96% Virality Score", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Optimal tags and high-affinity media detected.", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.publishPost(
                            type = selectedType,
                            title = title,
                            content = content.ifEmpty { "New drop on Omni!" },
                            tags = tags,
                            category = selectedCategory,
                            mediaRes = selectedMediaRes
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_post_submit_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish to ${selectedType.name} Feed", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RowScope.FormatSelectorPill(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.25f) else ObsidianSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) accentColor else ObsidianBorder),
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}
