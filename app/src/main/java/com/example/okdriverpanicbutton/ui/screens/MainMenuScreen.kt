package com.example.okdriverpanicbutton.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.data.ContactRepository
import com.example.okdriverpanicbutton.data.ServiceType
import com.example.okdriverpanicbutton.ui.components.EmergencyNumberDialog
import com.example.okdriverpanicbutton.ui.theme.IconBlue
import com.example.okdriverpanicbutton.ui.theme.IconGreen
import com.example.okdriverpanicbutton.ui.theme.IconRed
import com.example.okdriverpanicbutton.ui.theme.RosePrimary

/**
 * Main Menu screen with categorized list items for Members and Settings.
 * Each item shows a colored leading icon, title, subtitle, and trailing chevron.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToViewMembers: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToEditMessage: () -> Unit,
    contactRepository: ContactRepository,
    modifier: Modifier = Modifier
) {
    // Dialog states
    var showEmergencySettingsDialog by remember { mutableStateOf(false) }
    var showNumberDialog by remember { mutableStateOf(false) }
    var dialogServiceType by remember { mutableStateOf(ServiceType.POLICE) }
    var dialogNumberInput by remember { mutableStateOf("") }
    
    var emergencyCallsEnabled by remember { mutableStateOf(contactRepository.isEmergencyCallsEnabled()) }
    var currentTimerDuration by remember { mutableStateOf(contactRepository.getTimerDuration()) }
    var showTimerSheet by remember { mutableStateOf(false) }
    // Menu categories with items
    val membersItems = listOf(
        MenuItem(
            title = "Register Members",
            subtitle = "Add people who will receive your emergency SMS or email alerts",
            icon = Icons.Filled.PersonAdd,
            iconColor = IconGreen,
            onClick = onNavigateToRegister
        ),
        MenuItem(
            title = "View Members",
            subtitle = "View and manage your saved emergency contacts",
            icon = Icons.Filled.Visibility,
            iconColor = IconBlue,
            onClick = onNavigateToViewMembers
        ),
        MenuItem(
            title = "Emergency Calls",
            subtitle = "Quickly call to police, ambulance and fire.",
            icon = Icons.Filled.RadioButtonChecked,
            iconColor = IconRed,
            onClick = { showEmergencySettingsDialog = true }
        )
    )

    val settingsItems = listOf(
        MenuItem(
            title = "Edit SOS Message",
            subtitle = "Customize emergency message",
            icon = Icons.Filled.Edit,
            iconColor = IconRed,
            onClick = onNavigateToEditMessage
        ),
        MenuItem(
            title = "Edit Timer",
            subtitle = "Set delay before sending SOS",
            icon = Icons.Filled.Timer,
            iconColor = IconRed,
            onClick = { showTimerSheet = true }
        ),
        MenuItem(
            title = "SOS History",
            subtitle = "View previously sent alerts",
            icon = Icons.AutoMirrored.Filled.Chat,
            iconColor = IconBlue,
            onClick = onNavigateToHistory
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Main Menu",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RosePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Members section
            item {
                SectionHeader(title = "Members")
            }
            items(membersItems) { item ->
                MenuItemCard(item = item)
            }

            // Settings section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "Settings")
            }
            items(settingsItems) { item ->
                MenuItemCard(item = item)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showEmergencySettingsDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencySettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Enable Emergency Calls", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 18.sp)
                    Switch(
                        checked = emergencyCallsEnabled,
                        onCheckedChange = { 
                            emergencyCallsEnabled = it
                            contactRepository.setEmergencyCallsEnabled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = RosePrimary
                        )
                    )
                }
            },
            text = {
                Column {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ServiceItem("Police") {
                        dialogServiceType = ServiceType.POLICE
                        dialogNumberInput = contactRepository.getEmergencyNumber(ServiceType.POLICE) ?: ""
                        showNumberDialog = true
                    }
                    Divider()
                    ServiceItem("Ambulance") {
                        dialogServiceType = ServiceType.AMBULANCE
                        dialogNumberInput = contactRepository.getEmergencyNumber(ServiceType.AMBULANCE) ?: ""
                        showNumberDialog = true
                    }
                    Divider()
                    ServiceItem("Fire") {
                        dialogServiceType = ServiceType.FIRE
                        dialogNumberInput = contactRepository.getEmergencyNumber(ServiceType.FIRE) ?: ""
                        showNumberDialog = true
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEmergencySettingsDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    if (showNumberDialog) {
        EmergencyNumberDialog(
            serviceType = dialogServiceType,
            initialNumber = dialogNumberInput,
            onNumberChange = { dialogNumberInput = it },
            contactRepository = contactRepository,
            onDismiss = { showNumberDialog = false },
            onSaveSuccess = { /* Data is saved, PanicScreen will load it on resume */ },
            onDeleteSuccess = { /* Data is deleted */ }
        )
    }

    if (showTimerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimerSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Update SOS Timer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = { showTimerSheet = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = RosePrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                val timerOptions = listOf(3, 5, 10, 15, 20, 30)
                timerOptions.forEach { seconds ->
                    val isSelected = currentTimerDuration == seconds
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) RosePrimary.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable {
                                currentTimerDuration = seconds
                                contactRepository.setTimerDuration(seconds)
                                showTimerSheet = false
                            }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "$seconds Seconds",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) RosePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── Data Class ───────────────────────────────────────────────────────

private data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val onClick: () -> Unit
)

// ── Helper Composables ───────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(
            start = 16.dp,
            top = 16.dp,
            bottom = 8.dp
        )
    )
}

@Composable
private fun MenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { item.onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored icon circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.iconColor)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and subtitle
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            // Trailing chevron
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ServiceItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
