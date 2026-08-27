package com.example.ui.direct

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatConversationEntity
import com.example.data.model.ChatMessageEntity
import com.example.ui.OmniViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DirectScreen(
    conversations: List<ChatConversationEntity>,
    selectedConversationId: String?,
    activeMessages: List<ChatMessageEntity>,
    viewModel: OmniViewModel,
    modifier: Modifier = Modifier
) {
    if (selectedConversationId != null) {
        val currentConv = conversations.find { it.id == selectedConversationId }
        DirectChatThreadView(
            conversation = currentConv,
            messages = activeMessages,
            onBack = { viewModel.closeConversation() },
            onSendMessage = { text -> viewModel.sendChatMessage(selectedConversationId, text) },
            onStartCall = { isVideo -> viewModel.startCall(currentConv?.contactName ?: "Contact", isVideo) },
            modifier = modifier
        )
    } else {
        DirectConversationListView(
            conversations = conversations,
            onSelectConversation = { viewModel.openConversation(it.id) },
            modifier = modifier
        )
    }
}

@Composable
fun DirectConversationListView(
    conversations: List<ChatConversationEntity>,
    onSelectConversation: (ChatConversationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("direct_conversations_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // E2E Encryption Banner
        item {
            Surface(
                color = WhatsAppGreen.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("End-to-End Quantum Encrypted", color = WhatsAppGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Messages and calls are secured with zero-knowledge keys.", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Conversation items
        items(conversations) { conv ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectConversation(conv) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contact Avatar + Online Dot
                Box(contentAlignment = Alignment.BottomEnd) {
                    OmniMediaImage(
                        mediaUrl = conv.contactAvatarUrl,
                        contentDescription = conv.contactName,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                    if (conv.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreen)
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = conv.contactName,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (conv.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedBadge(size = 12)
                            }
                        }
                        Text(
                            text = conv.lastMessageTime,
                            color = if (conv.unreadCount > 0) WhatsAppGreen else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conv.lastMessage,
                            color = if (conv.unreadCount > 0) TextPrimary else TextSecondary,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (conv.unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = conv.unreadCount.toString(),
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Divider(color = ObsidianBorder.copy(alpha = 0.3f), thickness = 1.dp, modifier = Modifier.padding(start = 76.dp))
        }
    }
}

@Composable
fun DirectChatThreadView(
    conversation: ChatConversationEntity?,
    messages: List<ChatMessageEntity>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onStartCall: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Chat Header
        Surface(
            color = ObsidianSurface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }

                conversation?.let { conv ->
                    OmniMediaImage(
                        mediaUrl = conv.contactAvatarUrl,
                        contentDescription = conv.contactName,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = conv.contactName,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (conv.isOnline) "Online" else "Last seen recently",
                            color = if (conv.isOnline) WhatsAppGreen else TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = { onStartCall(true) }) {
                    Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = NeonCyan)
                }
                IconButton(onClick = { onStartCall(false) }) {
                    Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = NeonCyan)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(message = message)
            }
        }

        // Message Input Field
        Surface(
            color = ObsidianSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Emoji */ }) {
                    Icon(Icons.Default.Mood, contentDescription = "Emoji", tint = TextSecondary)
                }
                IconButton(onClick = { /* Attach */ }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = TextSecondary)
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Encrypted message...", color = TextMuted, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = WhatsAppGreen.copy(alpha = 0.6f),
                        unfocusedContainerColor = ObsidianSurfaceElevated,
                        focusedContainerColor = ObsidianSurfaceElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    containerColor = WhatsAppGreen,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp).testTag("direct_send_message_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity) {
    val isMe = message.isMe
    val bubbleColor = if (isMe) Color(0xFF005C4B) else ObsidianSurfaceElevated

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isMe) 14.dp else 2.dp,
                bottomEnd = if (isMe) 2.dp else 14.dp
            ),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (!isMe && message.senderName != "You") {
                    Text(
                        text = message.senderName,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                if (message.isVoiceNote) {
                    var isVoicePlaying by remember { mutableStateOf(false) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isVoicePlaying = !isVoicePlaying },
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(NeonCyan)
                        ) {
                            Icon(
                                if (isVoicePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        WaveformVisualizer(isPlaying = isVoicePlaying, barCount = 14)
                    }
                } else {
                    Text(
                        text = message.messageText,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timeFormatted,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = NeonCyan,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
