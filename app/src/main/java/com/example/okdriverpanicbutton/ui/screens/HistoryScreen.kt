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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.okdriverpanicbutton.data.HistoryRecord
import com.example.okdriverpanicbutton.data.HistoryRepository
import com.example.okdriverpanicbutton.data.HistoryStatus
import com.example.okdriverpanicbutton.ui.theme.AccentGreen
import com.example.okdriverpanicbutton.ui.theme.PanicRed
import com.example.okdriverpanicbutton.ui.theme.RosePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyRepository: HistoryRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var history by remember { mutableStateOf<List<HistoryRecord>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf<HistoryStatus?>(null) } // null means "All"

    LaunchedEffect(Unit) {
        history = historyRepository.getHistory()
    }

    val filteredHistory = remember(history, selectedFilter) {
        if (selectedFilter == null) history else history.filter { it.status == selectedFilter }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("SOS SMS List", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RosePrimary
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        text = "All",
                        isSelected = selectedFilter == null,
                        onClick = { selectedFilter = null }
                    )
                }
                item {
                    FilterChip(
                        text = "Accepted",
                        isSelected = selectedFilter == HistoryStatus.ACCEPTED,
                        onClick = { selectedFilter = HistoryStatus.ACCEPTED }
                    )
                }
                item {
                    FilterChip(
                        text = "Declined",
                        isSelected = selectedFilter == HistoryStatus.DECLINED,
                        onClick = { selectedFilter = HistoryStatus.DECLINED }
                    )
                }
                item {
                    FilterChip(
                        text = "Timed Out",
                        isSelected = selectedFilter == HistoryStatus.TIMED_OUT,
                        onClick = { selectedFilter = HistoryStatus.TIMED_OUT }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
            ) {
                items(filteredHistory) { record ->
                    HistoryListItem(record)
                    Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) RosePrimary else Color(0xFFF0F0F0))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.DarkGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun HistoryListItem(record: HistoryRecord) {
    val formatter = remember { SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault()) }
    val timeString = formatter.format(Date(record.timestamp))

    val (color, statusText) = when (record.status) {
        HistoryStatus.ACCEPTED -> Pair(AccentGreen, "Accepted")
        HistoryStatus.DECLINED -> Pair(PanicRed, "Declined")
        HistoryStatus.TIMED_OUT -> Pair(Color(0xFFF57C00), "Timed Out")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = record.userName.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = record.message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            lineHeight = 20.sp
        )
        
        Text(
            text = "Please reach ASAP to the below location",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            lineHeight = 20.sp
        )
        
        Text(
            text = "https://maps.google.com/maps?q=loc:24.433574,77.1601581",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A0DAB), // Google link blue
            textDecoration = TextDecoration.Underline,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = timeString,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )
    }
}
