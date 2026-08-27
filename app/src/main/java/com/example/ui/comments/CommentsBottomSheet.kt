package com.example.ui.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.CommentEntity
import com.example.ui.OmniViewModel
import com.example.ui.components.OmniMediaImage
import com.example.ui.pulse.formatCount
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    feedItemId: String,
    comments: List<CommentEntity>,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = ObsidianBorder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp)
                .testTag("comments_bottom_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments (${comments.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Divider(color = ObsidianBorder, thickness = 1.dp)

            // Comments List
            LazyColumn(
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No comments yet. Be the first to start the conversation!", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }

                items(comments) { comment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        OmniMediaImage(
                            mediaUrl = comment.authorAvatarUrl,
                            contentDescription = comment.authorName,
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(comment.authorName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(comment.timestampFormatted, color = TextMuted, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(comment.content, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                        IconButton(
                            onClick = { viewModel.toggleCommentLike(comment.id, comment.isLiked) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (comment.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (comment.isLiked) TikTokRed else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = ObsidianSurfaceElevated,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Add a comment...", color = TextMuted, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                viewModel.postComment(feedItemId, newCommentText)
                                newCommentText = ""
                            }
                        },
                        modifier = Modifier.size(38.dp).clip(CircleShape).background(NeonCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
