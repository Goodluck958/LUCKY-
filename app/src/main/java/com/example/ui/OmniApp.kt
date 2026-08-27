package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.admin.AdminPortalDialog
import com.example.ui.adstudio.AdStudioDialog
import com.example.ui.algorithm.AlgorithmTunerDialog
import com.example.ui.calls.CallDialog
import com.example.ui.comments.CommentsBottomSheet
import com.example.ui.components.OmniMediaImage
import com.example.ui.creator.CreatePostDialog
import com.example.ui.direct.DirectScreen
import com.example.ui.echoes.EchoesScreen
import com.example.ui.lounge.LoungeScreen
import com.example.ui.monetization.GiftSendDialog
import com.example.ui.monetization.MonetizationHubDialog
import com.example.ui.pulse.PulseScreen
import com.example.ui.settings.SettingsSecurityDialog
import com.example.ui.story.StoryViewerDialog
import com.example.ui.stream.StreamScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniApp(viewModel: OmniViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        topBar = {
            if (uiState.currentTab != OmniTab.PULSE || uiState.isSearching) {
                OmniTopAppBar(
                    currentTab = uiState.currentTab,
                    coinBalance = uiState.wallet.coinBalance,
                    onOpenAdmin = { viewModel.setAdminPortalVisible(true) },
                    onOpenSecurity = { viewModel.setSettingsSecurityVisible(true) },
                    onOpenMonetization = { viewModel.setMonetizationHubVisible(true) },
                    onOpenAlgorithm = { viewModel.setAlgorithmTunerVisible(true) },
                    onOpenAdStudio = { viewModel.setAdManagerVisible(true) }
                )
            } else {
                // For Pulse (Reels), show a floating translucent top overlay
                PulseFloatingTopBar(
                    coinBalance = uiState.wallet.coinBalance,
                    onOpenAdmin = { viewModel.setAdminPortalVisible(true) },
                    onOpenSecurity = { viewModel.setSettingsSecurityVisible(true) },
                    onOpenMonetization = { viewModel.setMonetizationHubVisible(true) },
                    onOpenAlgorithm = { viewModel.setAlgorithmTunerVisible(true) },
                    onOpenAdStudio = { viewModel.setAdManagerVisible(true) }
                )
            }
        },
        bottomBar = {
            // Respect navigation bars WindowInsets
            OmniBottomNavBar(
                currentTab = uiState.currentTab,
                unreadChatsCount = uiState.conversations.sumOf { it.unreadCount },
                onSelectTab = { viewModel.selectTab(it) },
                onOpenCreate = { viewModel.setCreatePostVisible(true) }
            )
        },
        floatingActionButton = {
            if (uiState.currentTab != OmniTab.PULSE) {
                FloatingActionButton(
                    onClick = { viewModel.setCreatePostVisible(true) },
                    containerColor = LuckyGold,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.testTag("omni_create_post_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Post")
                }
            }
        },
        snackbarHost = {
            uiState.toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ObsidianSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.8f)),
                        shadowElevation = 8.dp
                    ) {
                        Text(
                            text = msg,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (uiState.currentTab == OmniTab.PULSE) 0.dp else innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // Global Marquee Announcement Ticker (if any active broadcast exists)
            val activeAnnouncement = uiState.announcements.firstOrNull { it.isActive }
            if (activeAnnouncement != null && uiState.currentTab != OmniTab.PULSE) {
                Surface(
                    color = if (activeAnnouncement.type == "ALERT") SunsetPink.copy(alpha = 0.2f) else LuckyGoldDark.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (activeAnnouncement.type == "ALERT") SunsetPink else LuckyGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setAdminPortalVisible(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📢", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${activeAnnouncement.title}: ${activeAnnouncement.message}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                // Main 5-Platform Content
                when (uiState.currentTab) {
                    OmniTab.PULSE -> PulseScreen(
                        reels = uiState.reels,
                        viewModel = viewModel
                    )
                    OmniTab.STREAM -> StreamScreen(
                        streams = uiState.streams,
                        selectedVideo = uiState.selectedStreamVideo,
                        viewModel = viewModel
                    )
                    OmniTab.LOUNGE -> LoungeScreen(
                        feeds = uiState.feeds,
                        stories = uiState.stories,
                        viewModel = viewModel
                    )
                    OmniTab.ECHOES -> EchoesScreen(
                        sparks = uiState.sparks,
                        viewModel = viewModel
                    )
                    OmniTab.DIRECT -> DirectScreen(
                        conversations = uiState.conversations,
                        selectedConversationId = uiState.selectedConversationId,
                        activeMessages = uiState.activeChatMessages,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Super Admin Command Center Modal
    if (uiState.showAdminPortalModal) {
        AdminPortalDialog(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.setAdminPortalVisible(false) }
        )
    }

    // Security & Enterprise Privacy Modal
    if (uiState.showSettingsSecurityModal) {
        SettingsSecurityDialog(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.setSettingsSecurityVisible(false) }
        )
    }

    // Big-Tech Monetization Hub Modal
    if (uiState.showMonetizationHubModal) {
        MonetizationHubDialog(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.setMonetizationHubVisible(false) }
        )
    }

    // Virtual Creator Gift / Tip Dialog
    if (uiState.showGiftSendModal && uiState.giftTargetPost != null) {
        GiftSendDialog(
            targetPost = uiState.giftTargetPost,
            availableGifts = uiState.availableGifts,
            wallet = uiState.wallet,
            viewModel = viewModel,
            onDismiss = { viewModel.setGiftSendDialogVisible(false) }
        )
    }

    // Modals & Bottom Sheets
    if (uiState.showAlgorithmTunerModal) {
        AlgorithmTunerDialog(
            profile = uiState.algorithmProfile,
            viewModel = viewModel,
            onDismiss = { viewModel.setAlgorithmTunerVisible(false) }
        )
    }

    if (uiState.showAdManagerModal) {
        AdStudioDialog(
            campaigns = uiState.adCampaigns,
            viewModel = viewModel,
            onDismiss = { viewModel.setAdManagerVisible(false) }
        )
    }

    if (uiState.showCreatePostModal) {
        CreatePostDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setCreatePostVisible(false) }
        )
    }

    if (uiState.activeCommentPostId != null) {
        CommentsBottomSheet(
            feedItemId = uiState.activeCommentPostId!!,
            comments = uiState.selectedPostComments,
            viewModel = viewModel,
            onDismiss = { viewModel.closeComments() }
        )
    }

    if (uiState.activeStory != null) {
        StoryViewerDialog(
            story = uiState.activeStory!!,
            onDismiss = { viewModel.closeStory() }
        )
    }

    if (uiState.showCallDialog) {
        CallDialog(
            contactName = uiState.callContactName,
            isVideo = uiState.callIsVideo,
            onEndCall = { viewModel.endCall() }
        )
    }
}

@Composable
fun OmniTopAppBar(
    currentTab: OmniTab,
    coinBalance: Int,
    onOpenAdmin: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenMonetization: () -> Unit,
    onOpenAlgorithm: () -> Unit,
    onOpenAdStudio: () -> Unit
) {
    Surface(
        color = ObsidianSurface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title: Lucky ⏰
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = LuckyGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, LuckyGold)
                ) {
                    Text(
                        text = "⏰",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LUCKY",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = currentTab.title + " · " + currentTab.platformIconHint,
                        color = LuckyGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Control Portals (Admin, Security, Monetization, Algo, Ads)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Super Admin Command Button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = LuckyGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold),
                    onClick = onOpenAdmin,
                    modifier = Modifier.testTag("top_bar_admin_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = LuckyGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Admin", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Security & Settings
                IconButton(
                    onClick = onOpenSecurity,
                    modifier = Modifier.size(32.dp).testTag("top_bar_security_button")
                ) {
                    Icon(Icons.Default.Shield, contentDescription = "Security", tint = NeonCyan, modifier = Modifier.size(18.dp))
                }

                // Monetization / Wallet Hub
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ObsidianSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.4f)),
                    onClick = onOpenMonetization,
                    modifier = Modifier.testTag("top_bar_monetization_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⏰", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("$coinBalance", color = LuckyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Algorithm Brain Pill
                IconButton(
                    onClick = onOpenAlgorithm,
                    modifier = Modifier.size(32.dp).testTag("top_bar_algo_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Algorithm", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }

                // Ad Manager Pill
                IconButton(
                    onClick = onOpenAdStudio,
                    modifier = Modifier.size(32.dp).testTag("top_bar_ad_studio_button")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Ad Manager", tint = SunsetPink, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun PulseFloatingTopBar(
    coinBalance: Int,
    onOpenAdmin: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenMonetization: () -> Unit,
    onOpenAlgorithm: () -> Unit,
    onOpenAdStudio: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold)
            ) {
                Text(
                    text = "⏰",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LUCKY PULSE",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold),
                onClick = onOpenAdmin
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = LuckyGold, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Admin", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.5f)),
                onClick = onOpenMonetization
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏰", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("$coinBalance", color = LuckyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                onClick = onOpenAlgorithm
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.padding(5.dp).size(14.dp))
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SunsetPink.copy(alpha = 0.6f)),
                onClick = onOpenAdStudio
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = SunsetPink, modifier = Modifier.padding(5.dp).size(14.dp))
            }
        }
    }
}

@Composable
fun OmniBottomNavBar(
    currentTab: OmniTab,
    unreadChatsCount: Int,
    onSelectTab: (OmniTab) -> Unit,
    onOpenCreate: () -> Unit
) {
    NavigationBar(
        containerColor = ObsidianSurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier.testTag("omni_bottom_navigation_bar")
    ) {
        // 1. Pulse (TikTok / Reels)
        NavigationBarItem(
            selected = currentTab == OmniTab.PULSE,
            onClick = { onSelectTab(OmniTab.PULSE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == OmniTab.PULSE) Icons.Default.ElectricBolt else Icons.Outlined.ElectricBolt,
                    contentDescription = "Pulse",
                    tint = if (currentTab == OmniTab.PULSE) TikTokRed else TextSecondary
                )
            },
            label = { Text("Pulse", fontSize = 11.sp, fontWeight = if (currentTab == OmniTab.PULSE) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = TikTokRed.copy(alpha = 0.2f))
        )

        // 2. Stream (YouTube)
        NavigationBarItem(
            selected = currentTab == OmniTab.STREAM,
            onClick = { onSelectTab(OmniTab.STREAM) },
            icon = {
                Icon(
                    imageVector = if (currentTab == OmniTab.STREAM) Icons.Default.PlayCircleFilled else Icons.Outlined.PlayCircleOutline,
                    contentDescription = "Stream",
                    tint = if (currentTab == OmniTab.STREAM) YouTubeRed else TextSecondary
                )
            },
            label = { Text("Stream", fontSize = 11.sp, fontWeight = if (currentTab == OmniTab.STREAM) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = YouTubeRed.copy(alpha = 0.2f))
        )

        // 3. Lounge (Instagram / FB)
        NavigationBarItem(
            selected = currentTab == OmniTab.LOUNGE,
            onClick = { onSelectTab(OmniTab.LOUNGE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == OmniTab.LOUNGE) Icons.Default.PhotoCamera else Icons.Outlined.PhotoCamera,
                    contentDescription = "Lounge",
                    tint = if (currentTab == OmniTab.LOUNGE) SunsetPink else TextSecondary
                )
            },
            label = { Text("Lounge", fontSize = 11.sp, fontWeight = if (currentTab == OmniTab.LOUNGE) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = SunsetPink.copy(alpha = 0.2f))
        )

        // 4. Echoes (Twitter / X)
        NavigationBarItem(
            selected = currentTab == OmniTab.ECHOES,
            onClick = { onSelectTab(OmniTab.ECHOES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == OmniTab.ECHOES) Icons.Default.Forum else Icons.Outlined.Forum,
                    contentDescription = "Echoes",
                    tint = if (currentTab == OmniTab.ECHOES) TwitterBlue else TextSecondary
                )
            },
            label = { Text("Echoes", fontSize = 11.sp, fontWeight = if (currentTab == OmniTab.ECHOES) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = TwitterBlue.copy(alpha = 0.2f))
        )

        // 5. Direct (WhatsApp)
        NavigationBarItem(
            selected = currentTab == OmniTab.DIRECT,
            onClick = { onSelectTab(OmniTab.DIRECT) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadChatsCount > 0) {
                            Badge(containerColor = WhatsAppGreen, contentColor = Color.Black) {
                                Text(unreadChatsCount.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == OmniTab.DIRECT) Icons.Default.Chat else Icons.Outlined.Chat,
                        contentDescription = "Direct",
                        tint = if (currentTab == OmniTab.DIRECT) WhatsAppGreen else TextSecondary
                    )
                }
            },
            label = { Text("Direct", fontSize = 11.sp, fontWeight = if (currentTab == OmniTab.DIRECT) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = WhatsAppGreen.copy(alpha = 0.2f))
        )
    }
}
