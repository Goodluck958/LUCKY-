package com.example.ui.monetization

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.OmniViewModel
import com.example.ui.theme.*

enum class MonetizationTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SUBSCRIPTIONS("VIP Memberships", Icons.Default.WorkspacePremium),
    COIN_STORE("Coin Vault", Icons.Default.Savings),
    CREATOR_PAYOUTS("Creator Earnings", Icons.Default.Payments),
    AD_NETWORK("Ad Auction", Icons.Default.ShowChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationHubDialog(
    uiState: com.example.ui.OmniUiState,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MonetizationTab.SUBSCRIPTIONS) }

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
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(ObsidianSurface, LuckyGoldDark.copy(alpha = 0.3f))
                            )
                        )
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
                            Text(
                                text = "⏰",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LUCKY BIG-TECH MONETIZATION",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "Ad Auctions, VIP Passes, Gifting & Creator Payouts",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("monetization_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Balance summary banner
                Surface(
                    color = ObsidianSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Your Wallet Balance: ", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                "${uiState.wallet.coinBalance} ⏰ Coins",
                                color = LuckyGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LuckyGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = uiState.wallet.currentTier.title,
                                color = LuckyGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Tab Bar
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = ObsidianSurface,
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
                    MonetizationTab.values().forEach { tab ->
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

                // Tab Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    when (selectedTab) {
                        MonetizationTab.SUBSCRIPTIONS -> SubscriptionsTabContent(
                            currentTier = uiState.wallet.currentTier,
                            onUpgrade = { viewModel.upgradeSubscriptionTier(it) }
                        )
                        MonetizationTab.COIN_STORE -> CoinStoreTabContent(
                            onBuy = { coins, cost -> viewModel.buyCoinPackage(coins, cost) }
                        )
                        MonetizationTab.CREATOR_PAYOUTS -> CreatorPayoutsTabContent(
                            wallet = uiState.wallet,
                            onWithdraw = { viewModel.withdrawCreatorEarnings(it) }
                        )
                        MonetizationTab.AD_NETWORK -> AdNetworkTabContent(
                            adCampaigns = uiState.adCampaigns,
                            onOpenAdStudio = {
                                onDismiss()
                                viewModel.setAdManagerVisible(true)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionsTabContent(
    currentTier: SubscriptionTier,
    onUpgrade: (SubscriptionTier) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(SubscriptionTier.values()) { tier ->
            val isCurrent = tier == currentTier
            val gradientBrush = when (tier) {
                SubscriptionTier.LUCKY_BLACK -> Brush.horizontalGradient(listOf(Color(0xFF2E1A47), Color(0xFF5D3A9B)))
                SubscriptionTier.LUCKY_PLATINUM -> Brush.horizontalGradient(listOf(Color(0xFF1E3C72), Color(0xFF2A5298)))
                SubscriptionTier.LUCKY_GOLD -> Brush.horizontalGradient(listOf(Color(0xFF4A3B00), Color(0xFF7A6000)))
                SubscriptionTier.FREE -> Brush.horizontalGradient(listOf(ObsidianSurface, ObsidianSurfaceElevated))
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(
                    if (isCurrent) 2.dp else 1.dp,
                    if (isCurrent) LuckyGold else ObsidianBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBrush, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tier.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text(tier.priceFormatted, color = LuckyGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (isCurrent) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = LuckyGold
                            ) {
                                Text(
                                    "CURRENT PLAN",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = { onUpgrade(tier) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(10.dp))

                    tier.perks.forEach { perk ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = LuckyEmerald, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(perk, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinStoreTabContent(
    onBuy: (Int, Double) -> Unit
) {
    val packages = listOf(
        Triple(500, 4.99, "Starter Pack ⏰"),
        Triple(1200, 9.99, "Popular Pack (+20% Bonus) 🔥"),
        Triple(3500, 24.99, "Super Tipper (+40% Bonus) 💎"),
        Triple(10000, 69.99, "Whale VIP Pack (+50% Bonus) 👑")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Lucky Time Coins - In-App Tipping Economy", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text("Used for sending viral gifts, super-chats, and boosting creator streams.", color = TextSecondary, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(packages) { (coins, price, title) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("$coins ⏰ Coins", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                Text(title, color = LuckyGold, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = { onBuy(coins, price) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black)
                        ) {
                            Text("\$$price", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorPayoutsTabContent(
    wallet: UserWalletEntity,
    onWithdraw: (Double) -> Unit
) {
    var withdrawAmountText by remember { mutableStateOf(String.format("%.2f", wallet.creatorEarningsUsd)) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Earnings Balance Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, LuckyEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Available Creator Balance", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "\$${String.format("%,.2f", wallet.creatorEarningsUsd)}",
                        color = LuckyEmerald,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Lifetime Withdrawn", color = TextMuted, fontSize = 11.sp)
                            Text("\$${String.format("%,.2f", wallet.totalEarningsWithdrawnUsd)}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Platform Take-Rate", color = TextMuted, fontSize = 11.sp)
                            Text("${wallet.platformTakeRatePercent}%", color = LuckyGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            // Instant Cash-Out Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Request Direct Payout", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Disbursed automatically to Stripe Connect / US Bank / PayPal.", color = TextSecondary, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Payout Amount ($)") },
                        prefix = { Text("$ ") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val amount = withdrawAmountText.toDoubleOrNull() ?: 0.0
                            onWithdraw(amount)
                        },
                        enabled = wallet.creatorEarningsUsd > 0,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyEmerald, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_payout_button")
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Instant Payout to Bank", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdNetworkTabContent(
    adCampaigns: List<AdCampaignEntity>,
    onOpenAdStudio: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Ad Auction & Sponsor Engine", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Run targeted ads into Reels, YouTube streams, and X feeds.", color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Live Campaigns: ${adCampaigns.size}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = onOpenAdStudio,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Open Ad Manager", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                val totalImpressions = adCampaigns.sumOf { it.impressions }
                val totalClicks = adCampaigns.sumOf { it.clicks }
                val totalSpent = adCampaigns.sumOf { it.spentBudget }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Impressions", color = TextMuted, fontSize = 10.sp)
                        Text(String.format("%,d", totalImpressions), color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Clicks", color = TextMuted, fontSize = 10.sp)
                        Text(String.format("%,d", totalClicks), color = LuckyEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Total Ad Revenue", color = TextMuted, fontSize = 10.sp)
                        Text("\$${String.format("%.2f", totalSpent)}", color = LuckyGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
