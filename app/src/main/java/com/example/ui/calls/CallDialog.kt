package com.example.ui.calls

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.OmniMediaImage
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CallDialog(
    contactName: String,
    isVideo: Boolean,
    onEndCall: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }
    var isVideoOff by remember { mutableStateOf(!isVideo) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds += 1
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "callPulse"
    )

    Dialog(
        onDismissRequest = onEndCall,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .testTag("call_dialog")
        ) {
            // Background Visual
            if (isVideo && !isVideoOff) {
                OmniMediaImage(
                    mediaUrl = "drawable/ic_reel_cyber",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF1B2A4A), ObsidianBg)
                            )
                        )
                )
            }

            // Top Status Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = WhatsAppGreen.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("End-to-End Quantum Encrypted", color = WhatsAppGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(contactName, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%02d:%02d".format(callSeconds / 60, callSeconds % 60),
                    color = NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Center Avatar Graphic (if audio or video toggled)
            if (!isVideo || isVideoOff) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(3.dp, WhatsAppGreen, CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    OmniMediaImage(
                        mediaUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        contentDescription = contactName,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }
            }

            // Bottom Call Controls
            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = ObsidianSurface.copy(alpha = 0.9f),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) Color.White else ObsidianSurfaceElevated)
                    ) {
                        Icon(
                            if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) Color.Black else Color.White
                        )
                    }

                    // Video Toggle
                    if (isVideo) {
                        IconButton(
                            onClick = { isVideoOff = !isVideoOff },
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (isVideoOff) Color.White else ObsidianSurfaceElevated)
                        ) {
                            Icon(
                                if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Video",
                                tint = if (isVideoOff) Color.Black else Color.White
                            )
                        }
                    }

                    // Speaker Toggle
                    IconButton(
                        onClick = { isSpeakerOn = !isSpeakerOn },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isSpeakerOn) NeonCyan else ObsidianSurfaceElevated)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = if (isSpeakerOn) Color.Black else Color.White
                        )
                    }

                    // End Call
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE63946))
                            .testTag("end_call_button")
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}
