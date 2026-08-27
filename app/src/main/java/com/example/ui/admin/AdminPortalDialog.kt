package com.example.ui.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.OmniViewModel
import com.example.ui.theme.*

enum class AdminTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MODERATION("Content & Users", Icons.Default.Shield),
    BROADCAST("Global Broadcasts", Icons.Default.Campaign),
    REPORTS("Safety Reports", Icons.Default.ReportProblem),
    TELEMETRY("Live Telemetry", Icons.Default.Speed),
    ECONOMY("Revenue Controls", Icons.Default.MonetizationOn)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalDialog(
    uiState: com.example.ui.OmniUiState,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.MODERATION) }
    var showNewBroadcastDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg),
            color = ObsidianBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ObsidianSurface)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = LuckyGold.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Super Admin",
                                tint = LuckyGold,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SUPER ADMIN COMMAND",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = LuckyGoldDark
                                ) {
                                    Text(
                                        text = "ROOT",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Full Platform Governance & Network Control",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("admin_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Tab Bar
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = ObsidianSurfaceElevated,
                    contentColor = LuckyGold,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = LuckyGold,
                            height = 3.dp
                        )
                    }
                ) {
                    AdminTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(tab.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = tab.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            selectedContentColor = LuckyGold,
                            unselectedContentColor = TextSecondary
                        )
                    }
                }

                // Body based on selected tab
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    when (selectedTab) {
                        AdminTab.MODERATION -> ModerationSection(
                            reels = uiState.reels,
                            streams = uiState.streams,
                            feeds = uiState.feeds,
                            sparks = uiState.sparks,
                            viewModel = viewModel
                        )
                        AdminTab.BROADCAST -> BroadcastSection(
                            announcements = uiState.announcements,
                            viewModel = viewModel,
                            onOpenNew = { showNewBroadcastDialog = true }
                        )
                        AdminTab.REPORTS -> ReportsSection(
                            reports = uiState.reports,
                            viewModel = viewModel
                        )
                        AdminTab.TELEMETRY -> TelemetrySection(
                            algorithmProfile = uiState.algorithmProfile
                        )
                        AdminTab.ECONOMY -> EconomyControlSection(
                            wallet = uiState.wallet,
                            adCampaigns = uiState.adCampaigns,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    if (showNewBroadcastDialog) {
        CreateBroadcastDialog(
            viewModel = viewModel,
            onDismiss = { showNewBroadcastDialog = false }
        )
    }
}

@Composable
fun ModerationSection(
    reels: List<FeedItemEntity>,
    streams: List<FeedItemEntity>,
    feeds: List<FeedItemEntity>,
    sparks: List<FeedItemEntity>,
    viewModel: OmniViewModel
) {
    val allPosts = remember(reels, streams, feeds, sparks) {
        (reels + streams + feeds + sparks).sortedByDescending { it.timestamp }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Active Feed Post Governance (${allPosts.size} Total Live)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allPosts, key = { it.id }) { post ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (post.type) {
                                        FeedType.REEL -> TikTokRed.copy(alpha = 0.2f)
                                        FeedType.STREAM -> YouTubeRed.copy(alpha = 0.2f)
                                        FeedType.FEED -> SunsetPink.copy(alpha = 0.2f)
                                        FeedType.SPARK -> TwitterBlue.copy(alpha = 0.2f)
                                        else -> NeonCyan.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = post.type.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = post.authorName,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = post.authorHandle,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                if (post.authorVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = LuckyGold, modifier = Modifier.size(13.dp))
                                }
                            }

                            // Current Algorithm Score
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NeonCyan.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Score: ${String.format("%.2f", post.algorithmScore)}",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (post.title.isNotEmpty()) "${post.title}: ${post.content}" else post.content,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = ObsidianBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Super Admin Quick Control Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 5x Viral Velocity Boost
                            OutlinedButton(
                                onClick = { viewModel.adminBoostPost(post.id, 5.0f) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LuckyGold),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("5X Boost", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Toggle Verified Badge
                            OutlinedButton(
                                onClick = { viewModel.adminToggleVerified(post.authorHandle, post.authorVerified) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(if (post.authorVerified) "Unverify" else "Verify", fontSize = 11.sp)
                            }

                            // Ban / Nuke Post
                            Button(
                                onClick = { viewModel.adminDeletePost(post.id) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Nuke", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BroadcastSection(
    announcements: List<AdminAnnouncementEntity>,
    viewModel: OmniViewModel,
    onOpenNew: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Global Network Marquee Ticker",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onOpenNew,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp).testTag("admin_new_broadcast_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active global broadcasts. Click 'New Alert' to broadcast.", color = TextMuted, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(announcements, key = { it.id }) { ann ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ObsidianSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (ann.isActive) LuckyGold.copy(alpha = 0.5f) else ObsidianBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (ann.type == "ALERT") SunsetPink else LuckyGoldDark
                                    ) {
                                        Text(
                                            text = ann.type,
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = ann.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Switch(
                                    checked = ann.isActive,
                                    onCheckedChange = { viewModel.toggleAnnouncement(ann.id, ann.isActive) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = LuckyGold,
                                        uncheckedTrackColor = ObsidianBorder
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = ann.message, color = TextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.deleteAnnouncement(ann.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = TikTokRed, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", color = TikTokRed, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportsSection(
    reports: List<AdminReportEntity>,
    viewModel: OmniViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Content Safety & Moderation Queue (${reports.size} Pending)",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = LuckyEmerald, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Safety Queue Clean! No active flags.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reports, key = { it.id }) { rep ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ObsidianSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SunsetPink.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target: ${rep.targetAuthorHandle}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SunsetPink.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = rep.status,
                                        color = SunsetPink,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Reason: ${rep.reason}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "Reported by: ${rep.reporterHandle}", color = TextMuted, fontSize = 10.sp)

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.adminActionReport(rep.id, "DISMISSED") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Text("Dismiss", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { viewModel.adminActionReport(rep.id, "ACTIONED", rep.targetPostId) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Text("Take Down Post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetrySection(
    algorithmProfile: AlgorithmProfileEntity
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Live Server Nodes
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Global Infrastructure Status", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LuckyEmerald))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("100% HEALTHY", color = LuckyEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TelemetryCard(label = "Active Users", value = "1,842,910", tint = LuckyGold)
                        TelemetryCard(label = "Live Streams", value = "42,850", tint = YouTubeRed)
                        TelemetryCard(label = "Inference Latency", value = "3.2 ms", tint = NeonCyan)
                    }
                }
            }
        }

        item {
            // Region Map Nodes
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Edge Compute Nodes", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    NodeRow(region = "US-East (Virginia)", ping = "4ms", load = "28%", status = "OPTIMAL")
                    NodeRow(region = "EU-West (London)", ping = "11ms", load = "34%", status = "OPTIMAL")
                    NodeRow(region = "AP-East (Tokyo)", ping = "18ms", load = "41%", status = "OPTIMAL")
                    NodeRow(region = "SA-East (São Paulo)", ping = "24ms", load = "22%", status = "OPTIMAL")
                }
            }
        }
    }
}

@Composable
fun TelemetryCard(label: String, value: String, tint: Color) {
    Column(
        modifier = Modifier
            .background(ObsidianSurfaceElevated, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(label, color = TextMuted, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = tint, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun NodeRow(region: String, ping: String, load: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(region, color = TextPrimary, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ping: $ping", color = TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Load: $load", color = LuckyGold, fontSize = 10.sp)
        }
    }
}

@Composable
fun EconomyControlSection(
    wallet: UserWalletEntity,
    adCampaigns: List<AdCampaignEntity>,
    viewModel: OmniViewModel
) {
    var takeRate by remember { mutableStateOf(wallet.platformTakeRatePercent.toFloat()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Platform Take-Rate Controller
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Platform Take-Rate (Commission)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Big-Tech Standard: 30% (Apple/Google/YouTube/Twitch)", color = TextMuted, fontSize = 10.sp)
                        }
                        Text("${takeRate.toInt()}%", color = LuckyGold, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = takeRate,
                        onValueChange = { takeRate = it },
                        onValueChangeFinished = { viewModel.setPlatformTakeRate(takeRate.toInt()) },
                        valueRange = 10f..50f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = LuckyGold,
                            activeTrackColor = LuckyGold,
                            inactiveTrackColor = ObsidianBorder
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("10% (Creator Friendly)", color = TextMuted, fontSize = 10.sp)
                        Text("30% (Standard)", color = LuckyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("50% (Max Revenue)", color = SunsetPink, fontSize = 10.sp)
                    }
                }
            }
        }

        item {
            // Financial Ledger Snapshot
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Live Network Revenue Stream Snapshot", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    val adTotalSpend = adCampaigns.sumOf { it.spentBudget }
                    val grossRevenue = 184920.0 + adTotalSpend
                    val netProfit = grossRevenue * (takeRate / 100.0)

                    FinanceRow(label = "Gross Platform Volume (Ads + Coins + Subs)", value = "\$${String.format("%,.2f", grossRevenue)}", tint = Color.White)
                    FinanceRow(label = "Platform Net Revenue (${takeRate.toInt()}% Take-Rate)", value = "\$${String.format("%,.2f", netProfit)}", tint = LuckyGold)
                    FinanceRow(label = "Creator Pool Payouts", value = "\$${String.format("%,.2f", grossRevenue - netProfit)}", tint = LuckyEmerald)
                    FinanceRow(label = "Total Lucky Coin Transactions", value = "${wallet.totalCoinsPurchased} ⏰ coins", tint = NeonCyan)
                }
            }
        }
    }
}

@Composable
fun FinanceRow(label: String, value: String, tint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = tint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBroadcastDialog(
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ALERT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Global Network Broadcast", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Announcement Headline") },
                    placeholder = { Text("e.g. ⏰ LUCKY TIME: New Creator Reward Drop") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Broadcast Body Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALERT", "PROMO", "UPDATE").forEach { opt ->
                        FilterChip(
                            selected = type == opt,
                            onClick = { type = opt },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        viewModel.createAnnouncement(title, message, type)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black)
            ) {
                Text("Broadcast Now", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = ObsidianSurface
    )
}
