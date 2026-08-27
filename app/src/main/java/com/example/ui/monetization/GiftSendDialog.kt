package com.example.ui.monetization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.OmniViewModel
import com.example.ui.theme.*

@Composable
fun GiftSendDialog(
    targetPost: FeedItemEntity?,
    availableGifts: List<GiftItem>,
    wallet: UserWalletEntity,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    if (targetPost == null) return
    var selectedGift by remember { mutableStateOf<GiftItem?>(availableGifts.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, LuckyGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Send Creator Reward",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "To ${targetPost.authorName} (${targetPost.authorHandle})",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wallet balance pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ObsidianBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your Coin Balance:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            "${wallet.coinBalance} ⏰ Coins",
                            color = LuckyGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gifts Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableGifts) { gift ->
                        val isSelected = selectedGift?.id == gift.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) LuckyGold.copy(alpha = 0.15f) else ObsidianSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) LuckyGold else ObsidianBorder
                            ),
                            modifier = Modifier
                                .clickable { selectedGift = gift }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(gift.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    gift.name,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                                Text(
                                    "${gift.coinPrice} ⏰",
                                    color = LuckyGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Send Button
                Button(
                    onClick = {
                        selectedGift?.let { gift ->
                            viewModel.sendGift(gift, targetPost)
                        }
                    },
                    enabled = selectedGift != null && wallet.coinBalance >= (selectedGift?.coinPrice ?: 0),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuckyGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("send_gift_confirm_button")
                ) {
                    Text(
                        text = if (wallet.coinBalance < (selectedGift?.coinPrice ?: 0))
                            "Insufficient Coins (Need ${selectedGift?.coinPrice} ⏰)"
                        else
                            "Send ${selectedGift?.name ?: "Gift"} Now ✨",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
