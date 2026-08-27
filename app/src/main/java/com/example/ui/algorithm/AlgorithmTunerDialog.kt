package com.example.ui.algorithm

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AlgorithmProfileEntity
import com.example.ui.OmniViewModel
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AlgorithmTunerDialog(
    profile: AlgorithmProfileEntity,
    viewModel: OmniViewModel,
    onDismiss: () -> Unit
) {
    var techWeight by remember { mutableFloatStateOf(profile.techWeight) }
    var gamingWeight by remember { mutableFloatStateOf(profile.gamingWeight) }
    var humorWeight by remember { mutableFloatStateOf(profile.humorWeight) }
    var lifestyleWeight by remember { mutableFloatStateOf(profile.lifestyleWeight) }
    var musicWeight by remember { mutableFloatStateOf(profile.musicWeight) }
    var freshnessBias by remember { mutableFloatStateOf(profile.freshnessBias) }
    var echoChamberBreaker by remember { mutableFloatStateOf(profile.echoChamberBreaker) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = ObsidianSurface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .testTag("algorithm_tuner_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                                .background(NeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Neural Algorithm Tuner", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Real-Time Feed Weight Vector", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Controls & Spider Radar Graph
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Neural Radar Graph Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ObsidianSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Neural Affinity Web",
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NeuralRadarChart(
                                weights = listOf(techWeight, gamingWeight, humorWeight, lifestyleWeight, musicWeight),
                                labels = listOf("Tech", "Gaming", "Humor", "Life", "Music"),
                                modifier = Modifier.size(160.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Category Resonance Weights", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Sliders
                    AlgorithmSlider(label = "⚡ Tech & Artificial Intelligence", value = techWeight, color = NeonCyan) { techWeight = it }
                    AlgorithmSlider(label = "🎮 Gaming & Esports", value = gamingWeight, color = NeonPurple) { gamingWeight = it }
                    AlgorithmSlider(label = "😄 Humor, Comedy & Memes", value = humorWeight, color = BrightAmber) { humorWeight = it }
                    AlgorithmSlider(label = "🏔️ Lifestyle, Travel & Aesthetic", value = lifestyleWeight, color = SunsetPink) { lifestyleWeight = it }
                    AlgorithmSlider(label = "🎵 Music & Studio Sound", value = musicWeight, color = ElectricBlue) { musicWeight = it }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Exploration & Serendipity Controls", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    AlgorithmSlider(
                        label = "⚡ Freshness Bias (Prioritize Latest Drops)",
                        value = freshnessBias,
                        color = NeonGreen
                    ) { freshnessBias = it }

                    AlgorithmSlider(
                        label = "🌐 Echo Chamber Breaker (Discover Unseen Topics)",
                        value = echoChamberBreaker,
                        color = Color(0xFFFF70A6)
                    ) { echoChamberBreaker = it }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            techWeight = 0.5f
                            gamingWeight = 0.5f
                            humorWeight = 0.5f
                            lifestyleWeight = 0.5f
                            musicWeight = 0.5f
                            freshnessBias = 0.5f
                            echoChamberBreaker = 0.5f
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Reset", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.updateAlgorithmWeights(
                                tech = techWeight,
                                gaming = gamingWeight,
                                humor = humorWeight,
                                lifestyle = lifestyleWeight,
                                music = musicWeight,
                                freshness = freshnessBias,
                                echoChamberBreaker = echoChamberBreaker
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1.6f).testTag("algorithm_apply_button")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply & Re-Score", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AlgorithmSlider(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextSecondary, fontSize = 12.sp)
            Text("${(value * 100).toInt()}%", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = ObsidianBorder
            ),
            modifier = Modifier.height(28.dp)
        )
    }
}

@Composable
fun NeuralRadarChart(
    weights: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.4f
        val count = weights.size
        val angleStep = (2 * Math.PI / count).toFloat()

        // Draw web rings
        for (step in 1..4) {
            val r = radius * (step / 4f)
            val ringPath = Path()
            for (i in 0 until count) {
                val angle = i * angleStep - (Math.PI / 2).toFloat()
                val x = center.x + r * cos(angle)
                val y = center.y + r * sin(angle)
                if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
            }
            ringPath.close()
            drawPath(ringPath, color = ObsidianBorder, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f))
        }

        // Draw data polygon
        val dataPath = Path()
        for (i in 0 until count) {
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            val w = weights.getOrElse(i) { 0.5f }
            val r = radius * w.coerceIn(0.1f, 1f)
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()

        drawPath(
            dataPath,
            brush = Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.45f), NeonPurple.copy(alpha = 0.25f)), center = center)
        )
        drawPath(
            dataPath,
            color = NeonCyan,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )

        // Draw vertices
        for (i in 0 until count) {
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            val w = weights.getOrElse(i) { 0.5f }
            val r = radius * w.coerceIn(0.1f, 1f)
            val x = center.x + r * cos(angle)
            val y = center.y + r * sin(angle)
            drawCircle(color = NeonCyan, radius = 5f, center = Offset(x, y))
        }
    }
}
