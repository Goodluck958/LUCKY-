package com.example.ui.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.OmniViewModel
import com.example.ui.theme.*

enum class SettingsTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SECURITY("Security & Privacy", Icons.Default.Lock),
    DEVICES("Active Sessions", Icons.Default.Devices),
    PREFERENCES("App Experience", Icons.Default.Tune),
    DATA("Data & Vault", Icons.Default.Storage)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSecurityDialog(
    uiState: com.example.ui.OmniUiState,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(SettingsTab.SECURITY) }
    var config by remember { mutableStateOf(uiState.securityConfig) }

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
                        .background(ObsidianSurface)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = NeonCyan,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LUCKY ENTERPRISE SECURITY",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Zero-Knowledge Encryption & Session Vault",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("security_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Tab Bar
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = ObsidianSurfaceElevated,
                    contentColor = NeonCyan,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = NeonCyan,
                            height = 3.dp
                        )
                    }
                ) {
                    SettingsTab.values().forEach { tab ->
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
                            selectedContentColor = NeonCyan,
                            unselectedContentColor = TextSecondary
                        )
                    }
                }

                // Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    when (selectedTab) {
                        SettingsTab.SECURITY -> SecurityTabContent(
                            config = config,
                            onUpdate = { updated ->
                                config = updated
                                viewModel.updateSecurityConfig(updated)
                            }
                        )
                        SettingsTab.DEVICES -> DevicesTabContent(
                            sessions = uiState.activeSessions,
                            onRevoke = { viewModel.revokeSession(it) }
                        )
                        SettingsTab.PREFERENCES -> PreferencesTabContent(
                            config = config,
                            onUpdate = { updated ->
                                config = updated
                                viewModel.updateSecurityConfig(updated)
                            }
                        )
                        SettingsTab.DATA -> DataVaultTabContent(
                            onExport = { viewModel.exportPersonalData() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityTabContent(
    config: SecurityConfig,
    onUpdate: (SecurityConfig) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // E2E Encryption Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (config.e2eEncryptionEnabled) LuckyEmerald.copy(alpha = 0.5f) else ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("End-to-End Quantum Encryption", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = LuckyEmerald, modifier = Modifier.size(16.dp))
                            }
                            Text("All Direct chats, voice notes, and media keys are cryptographically signed on-device.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = config.e2eEncryptionEnabled,
                            onCheckedChange = { onUpdate(config.copy(e2eEncryptionEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LuckyEmerald)
                        )
                    }

                    if (config.e2eEncryptionEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ObsidianBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Device Safety Number Hash", color = TextMuted, fontSize = 10.sp)
                                    Text(config.quantumKeyHash, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            // Two-Factor Authentication (2FA)
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Two-Factor Authentication (2FA)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Requires TOTP authenticator verification when signing in from unknown browsers.", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = config.twoFactorAuthEnabled,
                            onCheckedChange = { onUpdate(config.copy(twoFactorAuthEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LuckyGold)
                        )
                    }

                    if (config.twoFactorAuthEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Active Authenticator Seed OTP: ${config.twoFactorOtpCode}", color = LuckyGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Biometric App Lock
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biometric Fingerprint / Face Unlock", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Require biometric authentication when switching apps after 60 seconds.", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = config.biometricLockEnabled,
                        onCheckedChange = { onUpdate(config.copy(biometricLockEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonCyan)
                    )
                }
            }
        }
    }
}

@Composable
fun DevicesTabContent(
    sessions: List<ActiveSessionItem>,
    onRevoke: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Logged In Active Device Nodes (${sessions.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Terminate suspicious or unrecognized sessions immediately with instant key destruction.", color = TextSecondary, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sessions, key = { it.id }) { session ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ObsidianSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (session.isCurrentDevice) LuckyEmerald.copy(alpha = 0.4f) else ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (session.isCurrentDevice) LuckyEmerald.copy(alpha = 0.2f) else ObsidianSurfaceElevated
                            ) {
                                Icon(
                                    imageVector = if (session.deviceType == "Mobile") Icons.Default.PhoneAndroid else if (session.deviceType == "Desktop") Icons.Default.Laptop else Icons.Default.Dns,
                                    contentDescription = null,
                                    tint = if (session.isCurrentDevice) LuckyEmerald else TextSecondary,
                                    modifier = Modifier.padding(8.dp).size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(session.deviceName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    if (session.isCurrentDevice) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = LuckyEmerald) {
                                            Text("THIS DEVICE", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                Text("${session.location} · ${session.ipAddress}", color = TextSecondary, fontSize = 11.sp)
                                Text(session.lastActive, color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        if (!session.isCurrentDevice) {
                            Button(
                                onClick = { onRevoke(session.id) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Revoke", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreferencesTabContent(
    config: SecurityConfig,
    onUpdate: (SecurityConfig) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Stealth / Ghost Mode
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Stealth / Ghost Mode", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Hide online presence, typing status, and story viewing history across all tabs.", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = config.ghostModeEnabled,
                        onCheckedChange = { onUpdate(config.copy(ghostModeEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = SunsetPink)
                    )
                }
            }
        }

        item {
            // Disappearing Messages
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Ephemeral / Disappearing Messages", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Auto-erase chat records after the selected duration expires.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Off", 24 to "24 Hours", 168 to "7 Days").forEach { (hours, label) ->
                            FilterChip(
                                selected = config.disappearingMessagesHours == hours,
                                onClick = { onUpdate(config.copy(disappearingMessagesHours = hours)) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        item {
            // Content Shield
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ObsidianSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("AI Content Shield & Safety Filtering", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Neural filter level for automated hate speech and spam moderation.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("STRICT", "BALANCED", "UNFILTERED").forEach { lvl ->
                            FilterChip(
                                selected = config.contentShieldLevel == lvl,
                                onClick = { onUpdate(config.copy(contentShieldLevel = lvl)) },
                                label = { Text(lvl, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataVaultTabContent(
    onExport: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = LuckyGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Export All Personal Data (GDPR Vault)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Download your complete post history, encrypted message backups, and algorithm interaction matrix.", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onExport,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().testTag("export_data_button")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Encrypted Archive (.ZIP)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
