package com.example.ui.adstudio

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AdCampaignEntity
import com.example.data.model.AdObjective
import com.example.data.model.TopicCategory
import com.example.ui.OmniViewModel
import com.example.ui.components.OmniMediaImage
import com.example.ui.pulse.formatCount
import com.example.ui.theme.*

@Composable
fun AdStudioDialog(
    campaigns: List<AdCampaignEntity>,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Analytics, 1: Campaigns, 2: Create Ad

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SunsetPink.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("ad_studio_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SunsetPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = SunsetPink, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Omni Ad Studio & Manager", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Self-Serve Monetization Engine", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ObsidianSurfaceElevated,
                    contentColor = SunsetPink,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SunsetPink
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Analytics & ROI", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Active Ads (${campaigns.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("+ Launch Ad", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> AdAnalyticsView(campaigns = campaigns)
                        1 -> ActiveCampaignsListView(
                            campaigns = campaigns,
                            onToggle = { id, active -> viewModel.toggleCampaign(id, active) },
                            onCreateNew = { selectedTab = 2 }
                        )
                        2 -> CreateAdCampaignWizard(
                            viewModel = viewModel,
                            onSuccess = { selectedTab = 1 }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdAnalyticsView(campaigns: List<AdCampaignEntity>) {
    val totalImpressions = campaigns.sumOf { it.impressions }
    val totalClicks = campaigns.sumOf { it.clicks }
    val totalSpent = campaigns.sumOf { it.spentBudget }
    val totalConversions = campaigns.sumOf { it.conversions }
    val ctr = if (totalImpressions > 0) (totalClicks.toDouble() / totalImpressions * 100) else 0.0
    val cpc = if (totalClicks > 0) (totalSpent / totalClicks) else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-level Performance Metrics Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Total Ad Spend",
                value = "$%.2f".format(totalSpent),
                color = SunsetPink,
                subtext = "+14% vs yesterday",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total Impressions",
                value = formatCount(totalImpressions),
                color = NeonCyan,
                subtext = "Omni Multi-feed reach",
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Avg. CTR",
                value = "%.2f%%".format(ctr),
                color = BrightAmber,
                subtext = "Industry high (Avg 1.8%)",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Avg. CPC",
                value = "$%.2f".format(cpc),
                color = NeonGreen,
                subtext = "Optimized bidding",
                modifier = Modifier.weight(1f)
            )
        }

        // Live ROI & Conversion Highlights
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ObsidianSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Live Conversion Attribution", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = NeonGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("ROAS 4.8x", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Total Completed Conversions: $totalConversions orders/installs generated across Pulse & Stream feeds.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { 0.72f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = SunsetPink,
                    trackColor = ObsidianBorder
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ObsidianSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun ActiveCampaignsListView(
    campaigns: List<AdCampaignEntity>,
    onToggle: (String, Boolean) -> Unit,
    onCreateNew: () -> Unit
) {
    if (campaigns.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No active campaigns", color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCreateNew, colors = ButtonDefaults.buttonColors(containerColor = SunsetPink)) {
                    Text("Create First Ad Campaign")
                }
            }
        }
        return
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(campaigns) { campaign ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ObsidianSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(campaign.campaignName, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Brand: ${campaign.brandName} · Goal: ${campaign.objective.name}", color = TextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = campaign.isActive,
                            onCheckedChange = { onToggle(campaign.id, campaign.isActive) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SunsetPink
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Budget: $${campaign.dailyBudget.toInt()}/day", color = TextSecondary, fontSize = 12.sp)
                        Text("Spent: $${"%.2f".format(campaign.spentBudget)}", color = SunsetPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Clicks: ${campaign.clicks}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateAdCampaignWizard(
    viewModel: OmniViewModel,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("Quantum Sonic Pro Launch") }
    var brand by remember { mutableStateOf("Apex Audio Global") }
    var objective by remember { mutableStateOf(AdObjective.APP_INSTALLS) }
    var dailyBudget by remember { mutableStateOf("120") }
    var headline by remember { mutableStateOf("Experience 360° Spatial Audio") }
    var bodyCopy by remember { mutableStateOf("Claim 40% launch discount on the revolutionary neural acoustic earbuds.") }
    var ctaText by remember { mutableStateOf("Shop 40% Off") }
    var category by remember { mutableStateOf(TopicCategory.TECH) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Campaign Name", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
        )

        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text("Brand / Advertiser Name", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
        )

        OutlinedTextField(
            value = dailyBudget,
            onValueChange = { dailyBudget = it },
            label = { Text("Daily Budget ($ USD)", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
        )

        // AI Copywriter Button
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = NeonPurple.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.6f)),
            onClick = {
                headline = "Next-Gen AI Tech Designed For 2026 🚀"
                bodyCopy = "Unlock quantum speed workflows and instant neural curation. Try it free today!"
                ctaText = "Get Started Now"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("✨ Generate High-Converting AI Ad Copy", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        OutlinedTextField(
            value = headline,
            onValueChange = { headline = it },
            label = { Text("Ad Headline", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
        )

        OutlinedTextField(
            value = bodyCopy,
            onValueChange = { bodyCopy = it },
            label = { Text("Ad Description / Body Copy", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
        )

        OutlinedTextField(
            value = ctaText,
            onValueChange = { ctaText = it },
            label = { Text("Call to Action (CTA) Button Text", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.createAdCampaign(
                    name = name,
                    brand = brand,
                    objective = objective,
                    dailyBudget = dailyBudget.toDoubleOrNull() ?: 50.0,
                    headline = headline,
                    body = bodyCopy,
                    ctaText = ctaText,
                    ctaUrl = "https://example.com/ad",
                    category = category,
                    mediaUrl = "drawable/ic_ad_banner"
                )
                onSuccess()
            },
            colors = ButtonDefaults.buttonColors(containerColor = SunsetPink, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("ad_launch_campaign_button")
        ) {
            Icon(Icons.Default.RocketLaunch, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Launch Campaign & Deploy To Feeds", fontWeight = FontWeight.Bold)
        }
    }
}
