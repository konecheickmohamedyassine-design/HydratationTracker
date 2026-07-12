package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WaterIntake
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WaterTrackerScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val totalConsumed by viewModel.totalConsumedToday.collectAsStateWithLifecycle()
    val remainingMl by viewModel.remainingMlToday.collectAsStateWithLifecycle()
    val progressPercent by viewModel.progressPercent.collectAsStateWithLifecycle()
    val historyList by viewModel.allIntakes.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val todayDateString = remember {
        val sdf = SimpleDateFormat("EEEE d MMMM", Locale.getDefault())
        sdf.format(Date()).replaceFirstChar { it.uppercase() }
    }

    var showGoalSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DeepDarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MONITOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = NeonTurquoise.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "HydraTrack",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = TextWhite,
                        modifier = Modifier.testTag("app_title")
                    )
                }

                // Pulsing Online Glow Indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(LightDarkSurface, shape = CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
                    val glowScale by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "glow_scale"
                    )
                    val glowAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "glow_alpha"
                    )

                    // Outer halo
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(glowScale)
                            .background(NeonTurquoise.copy(alpha = 0.3f * glowAlpha), shape = CircleShape)
                    )
                    // Inner solid
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(NeonTurquoise, shape = CircleShape)
                    )
                }
            }

            // Scrollable Bento Grid Body
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today Subtitle & Motivation Info
                item {
                    Text(
                        text = todayDateString,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = TextGray
                    )
                }

                // Progress Circle Bento Card (Cell 1)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = NeonTurquoise.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = DeepDarkSurface
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Radial background glow behind circle
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                NeonTurquoise.copy(alpha = 0.1f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                GlowingCircleProgress(
                                    progress = progressPercent,
                                    consumed = totalConsumed,
                                    goal = viewModel.dailyGoalMl
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Objectif restant",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TextGray
                                )
                                Text(
                                    text = "$remainingMl ml",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextWhite,
                                    modifier = Modifier.testTag("remaining_amount_text")
                                )
                            }
                        }
                    }
                }

                // Mini Stats Bento Cards (Cells 2 & 3 - Side-by-side)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left Bento Card: Streak (Série)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = DeepDarkSurface),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SÉRIE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextGray
                                )
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "$streakDays",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = TextWhite
                                    )
                                    Text(
                                        text = if (streakDays > 1) "jours" else "jour",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextGray
                                    )
                                }
                            }
                        }

                        // Right Bento Card: Status (Statut)
                        val isOptimal = totalConsumed >= viewModel.dailyGoalMl
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(90.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = DeepDarkSurface),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "STATUT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextGray
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = if (isOptimal) NeonTurquoise else Color(0xFFFF5252),
                                                shape = CircleShape
                                            )
                                    )
                                    Text(
                                        text = if (isOptimal) "Optimal" else "Insuffisant",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isOptimal) NeonTurquoise else TextWhite,
                                        modifier = Modifier.testTag("goal_status_text")
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick add selector within grid
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        listOf(100, 250, 500).forEach { amount ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 1.dp,
                                        color = NeonTurquoise.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DeepDarkSurface)
                                    .clickable { viewModel.addWater(amount) }
                                    .padding(vertical = 10.dp)
                                    .testTag("quick_add_${amount}_chip"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+$amount ml",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = NeonTurquoise
                                )
                            }
                        }
                    }
                }

                // History Bento Card (Cell 4 - List & Header)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HISTORIQUE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = TextGray
                        )
                        Text(
                            text = "${historyList.size} AJOUT${if (historyList.size > 1) "S" else ""}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonTurquoise
                        )
                    }
                }

                if (historyList.isEmpty()) {
                    item {
                        EmptyBentoState()
                    }
                } else {
                    items(
                        items = historyList,
                        key = { it.id }
                    ) { intake ->
                        HistoryBentoItem(
                            intake = intake,
                            onDelete = { viewModel.deleteIntake(intake.id) }
                        )
                    }
                }

                // Safe spacer at the bottom
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Footer Controls Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Trigger Button: Column-Based Reset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.resetToday() }
                        .testTag("reset_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(LightDarkSurface, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RESET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TextGray
                    )
                }

                // Center Action Button: Rising Turquoise Glow +
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonTurquoise.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { viewModel.addWater(250) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonTurquoise,
                            contentColor = DeepDarkBackground
                        ),
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("add_water_button"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter 250ml",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Right Trigger Button: Column-Based Target / Info Dialog
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showGoalSettingsDialog = true }
                        .testTag("settings_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(LightDarkSurface, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Paramètres",
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "INFOS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TextGray
                    )
                }
            }
        }
    }

    // Modal Details Info Dialog
    if (showGoalSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showGoalSettingsDialog = false },
            confirmButton = {
                TextButton(onClick = { showGoalSettingsDialog = false }) {
                    Text("FERMER", color = NeonTurquoise, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Conseils d'Hydratation",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• L'objectif par défaut de 2 L est recommandé pour maintenir l'énergie corporelle et la concentration.",
                        color = TextWhite
                    )
                    Text(
                        text = "• Buvez par petites gorgées régulières plutôt que de grandes quantités d'un coup.",
                        color = TextWhite
                    )
                    Text(
                        text = "• Votre série consecutive est mise à jour chaque jour où vous enregistrez une consommation.",
                        color = TextWhite
                    )
                }
            },
            containerColor = DeepDarkSurface,
            textContentColor = TextWhite,
            titleContentColor = TextWhite,
            modifier = Modifier.border(1.dp, NeonTurquoise.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
        )
    }
}

@Composable
fun GlowingCircleProgress(
    progress: Float,
    consumed: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radial_progress"
    )

    val animatedAmount by animateIntAsState(
        targetValue = consumed,
        animationSpec = tween(durationMillis = 800),
        label = "amount_anim"
    )

    var bentoScale by remember { mutableStateOf(1f) }
    val bentoScaleAnim by animateFloatAsState(
        targetValue = bentoScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = { if (bentoScale > 1f) bentoScale = 1f },
        label = "bento_spring"
    )

    LaunchedEffect(consumed) {
        if (consumed > 0) {
            bentoScale = 1.08f
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(200.dp)
            .scale(bentoScaleAnim)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeTrack = 10.dp.toPx()
            val strokeProgress = 12.dp.toPx()
            val radius = (size.minDimension - strokeProgress) / 2

            // Track background
            drawCircle(
                color = LightDarkSurface,
                radius = radius,
                style = Stroke(width = strokeTrack)
            )

            if (animatedProgress > 0f) {
                val sweep = (animatedProgress * 360f).coerceAtMost(360f)

                // High shadow/glow halo
                drawArc(
                    color = NeonTurquoise.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                )

                // Bright core turquoise arc
                drawArc(
                    color = NeonTurquoise,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeProgress, cap = StrokeCap.Round)
                )
            }
        }

        // Percentage Text center info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val percent = if (goal > 0) ((consumed.toFloat() / goal.toFloat()) * 100).toInt() else 0

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$animatedAmount ml",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = TextWhite,
                    modifier = Modifier.testTag("consumed_amount_text")
                )
                Text(
                    text = " / ${goal / 1000} L",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonTurquoise
                ),
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 8f
                }
            )
        }
    }
}

@Composable
fun EmptyBentoState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = DeepDarkSurface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Opacity,
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.4f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Aucun ajout",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ajoutez de l'eau pour suivre votre parcours d'hydratation.",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HistoryBentoItem(
    intake: WaterIntake,
    onDelete: () -> Unit
) {
    val timeString = remember(intake.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(intake.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.03f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = LightDarkSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // High contrast turquoise badge container
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(NeonTurquoiseGlow, shape = CircleShape)
                        .border(1.dp, NeonTurquoise.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = NeonTurquoise,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Detail specs
                Column {
                    Text(
                        text = "+${intake.amountMl} ml",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextWhite,
                        modifier = Modifier.testTag("history_item_amount_${intake.id}")
                    )
                    Text(
                        text = "Aujourd'hui, $timeString",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            // Symmetrical delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_button_${intake.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = TextGray.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
