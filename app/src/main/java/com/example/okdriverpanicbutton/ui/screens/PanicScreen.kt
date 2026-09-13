package com.example.okdriverpanicbutton.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.data.ContactRepository
import com.example.okdriverpanicbutton.data.ServiceType
import com.example.okdriverpanicbutton.ui.components.ConfirmedCard
import com.example.okdriverpanicbutton.ui.components.CountdownOverlay
import com.example.okdriverpanicbutton.ui.components.EmergencyNumberDialog
import com.example.okdriverpanicbutton.ui.components.PanicButton
import com.example.okdriverpanicbutton.ui.components.SearchingIndicator
import com.example.okdriverpanicbutton.ui.components.UserRequestCard
import com.example.okdriverpanicbutton.ui.theme.PanicRed
import com.example.okdriverpanicbutton.ui.theme.RosePrimary
import com.example.okdriverpanicbutton.ui.theme.SurfaceDark
import com.example.okdriverpanicbutton.ui.theme.SurfaceDeep
import com.example.okdriverpanicbutton.ui.theme.TextSecondary
import com.example.okdriverpanicbutton.viewmodel.HapticType
import com.example.okdriverpanicbutton.viewmodel.PanicState
import com.example.okdriverpanicbutton.viewmodel.PanicViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanicScreen(
    viewModel: PanicViewModel,
    contactRepository: ContactRepository,
    onNavigateToMenu: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showNumberDialog by remember { mutableStateOf(false) }
    var dialogServiceType by remember { mutableStateOf(ServiceType.POLICE) }
    var dialogNumberInput by remember { mutableStateOf("") }

    var policeNumber by remember { mutableStateOf(contactRepository.getEmergencyNumber(ServiceType.POLICE)) }
    var ambulanceNumber by remember { mutableStateOf(contactRepository.getEmergencyNumber(ServiceType.AMBULANCE)) }
    var fireNumber by remember { mutableStateOf(contactRepository.getEmergencyNumber(ServiceType.FIRE)) }

    LaunchedEffect(Unit) {
        viewModel.hapticEvent.collect { hapticType ->
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(VibratorManager::class.java)
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }

            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = when (hapticType) {
                        HapticType.HEAVY -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        HapticType.MEDIUM -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                        HapticType.LIGHT -> VibrationEffect.createOneShot(20, 80)
                        HapticType.SUCCESS -> VibrationEffect.createWaveform(
                            longArrayOf(0, 50, 100, 50), intArrayOf(0, 200, 0, 255), -1
                        )
                        HapticType.ERROR -> VibrationEffect.createWaveform(
                            longArrayOf(0, 80, 60, 80), intArrayOf(0, 255, 0, 255), -1
                        )
                    }
                    v.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(50)
                }
            }
        }
    }

    // ── Snackbar for "no responders" ─────────────────────────────────
    LaunchedEffect(state) {
        val currentState = state
        if (currentState is PanicState.Idle && currentState.noRespondersMessage) {
            snackbarHostState.showSnackbar("No responders available. Try again later.")
        }
    }

    // Whether the SOS flow overlay should be visible
    val showOverlay = state !is PanicState.Idle

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SOS Alert",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToMenu) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToRegister) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RosePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = RosePrimary.copy(alpha = 0.9f),
                        contentColor = Color.White
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Main Content ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Service icons row (Police, Ambulance, Fire) — clickable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceIcon(
                        icon = Icons.Filled.LocalPolice,
                        label = "Police",
                        savedNumber = policeNumber,
                        tint = Color(0xFF1565C0),
                        onClick = {
                            val num = policeNumber
                            if (num.isNullOrBlank()) {
                                // No number saved — open dialog to add
                                dialogServiceType = ServiceType.POLICE
                                dialogNumberInput = ""
                                showNumberDialog = true
                            } else {
                                // Number exists — dial it
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                                context.startActivity(intent)
                            }
                        }
                    )
                    ServiceIcon(
                        icon = Icons.Filled.LocalHospital,
                        label = "Ambulance",
                        savedNumber = ambulanceNumber,
                        tint = Color(0xFF2E7D32),
                        onClick = {
                            val num = ambulanceNumber
                            if (num.isNullOrBlank()) {
                                dialogServiceType = ServiceType.AMBULANCE
                                dialogNumberInput = ""
                                showNumberDialog = true
                            } else {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                                context.startActivity(intent)
                            }
                        }
                    )
                    ServiceIcon(
                        icon = Icons.Filled.LocalFireDepartment,
                        label = "Fire",
                        savedNumber = fireNumber,
                        tint = Color(0xFFE53935),
                        onClick = {
                            val num = fireNumber
                            if (num.isNullOrBlank()) {
                                dialogServiceType = ServiceType.FIRE
                                dialogNumberInput = ""
                                showNumberDialog = true
                            } else {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
                                context.startActivity(intent)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Giant SOS Button
                PanicButton(
                    onPress = { viewModel.onPanicPressed(contactRepository.getTimerDuration()) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Instruction text
                Text(
                    text = "PRESS THE BUTTON IN CASE OF\nEMERGENCY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 40.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
            }

            AnimatedVisibility(
                visible = showOverlay,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SurfaceDark.copy(alpha = 0.97f),
                                    SurfaceDeep.copy(alpha = 0.97f)
                                )
                            )
                        )
                ) {
                    AnimatedContent(
                        targetState = state,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        contentKey = { it::class },
                        label = "sosFlowTransition",
                        modifier = Modifier.fillMaxSize()
                    ) { currentState ->
                        when (currentState) {
                            is PanicState.Idle -> {
                                // Shouldn't show — overlay hides on Idle
                                Box(modifier = Modifier.fillMaxSize())
                            }

                            is PanicState.Countdown -> {
                                CountdownOverlay(
                                    secondsRemaining = currentState.secondsRemaining,
                                    totalSeconds = currentState.totalSeconds,
                                    onCancel = { viewModel.onCancelCountdown() }
                                )
                            }

                            is PanicState.Searching -> {
                                SearchingIndicator()
                            }

                            is PanicState.RequestSent -> {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 16.dp)
                                    ) {
                                        SimulatedNotificationCard(
                                            senderName = currentState.user.name,
                                            message = contactRepository.getSosMessage()
                                        )
                                    }

                                    Box(
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        UserRequestCard(
                                            user = currentState.user,
                                            distance = currentState.distance,
                                            onYes = { viewModel.onUserResponds(accepted = true) },
                                            onNo = { viewModel.onUserResponds(accepted = false) }
                                        )
                                    }
                                }
                            }

                            is PanicState.Declined -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = currentState.user.avatarEmoji,
                                            fontSize = 40.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "${currentState.user.name} declined",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Finding next helper...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            is PanicState.Accepted -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ConfirmedCard(
                                        user = currentState.user,
                                        eta = currentState.eta,
                                        location = currentState.location,
                                        onDone = { viewModel.onReset() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val currentState = state
    if (currentState is PanicState.Idle && currentState.noRegisteredMembersError) {
        AlertDialog(
            onDismissRequest = { viewModel.clearErrors() },
            title = {
                Text("No Registered Members", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("You haven't registered any member to help you. Please go to the Main Menu and add members before using the SOS feature.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearErrors() }) {
                    Text("OK", color = RosePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ── Emergency Number Dialog ───────────────────────────────────────
    if (showNumberDialog) {
        EmergencyNumberDialog(
            serviceType = dialogServiceType,
            initialNumber = dialogNumberInput,
            onNumberChange = { dialogNumberInput = it },
            contactRepository = contactRepository,
            onDismiss = { showNumberDialog = false },
            onSaveSuccess = { num ->
                when (dialogServiceType) {
                    ServiceType.POLICE -> policeNumber = num
                    ServiceType.AMBULANCE -> ambulanceNumber = num
                    ServiceType.FIRE -> fireNumber = num
                }
            },
            onDeleteSuccess = {
                when (dialogServiceType) {
                    ServiceType.POLICE -> policeNumber = null
                    ServiceType.AMBULANCE -> ambulanceNumber = null
                    ServiceType.FIRE -> fireNumber = null
                }
            }
        )
    }
}

// ── Helper Composables ───────────────────────────────────────────────

/**
 * A service icon with label and optional saved number.
 * Tap: if number saved → dial; if not → open edit dialog.
 */
@Composable
private fun ServiceIcon(
    icon: ImageVector,
    label: String,
    savedNumber: String?,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        // Show saved number below label
        if (!savedNumber.isNullOrBlank()) {
            Text(
                text = savedNumber,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SimulatedNotificationCard(
    senderName: String,
    message: String
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFFF0F0F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = RosePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SOS Help Request • now",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$senderName — $message",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}
