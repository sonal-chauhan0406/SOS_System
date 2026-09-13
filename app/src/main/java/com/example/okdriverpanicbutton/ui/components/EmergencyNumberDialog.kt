package com.example.okdriverpanicbutton.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.okdriverpanicbutton.data.ContactRepository
import com.example.okdriverpanicbutton.data.ServiceType
import com.example.okdriverpanicbutton.ui.theme.RosePrimary

@Composable
fun EmergencyNumberDialog(
    serviceType: ServiceType,
    initialNumber: String,
    onNumberChange: (String) -> Unit,
    contactRepository: ContactRepository,
    onDismiss: () -> Unit,
    onSaveSuccess: (String) -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${serviceType.label} Number",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the ${serviceType.label.lowercase()} emergency number:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                val isError = initialNumber.isNotBlank() && !initialNumber.all { it.isDigit() }
                
                OutlinedTextField(
                    value = initialNumber,
                    onValueChange = onNumberChange,
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = isError,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RosePrimary,
                        focusedLabelColor = RosePrimary,
                        cursorColor = RosePrimary
                    )
                )
                
                if (isError) {
                    Text(
                        text = "Only numbers are allowed",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            val isError = initialNumber.isNotBlank() && !initialNumber.all { it.isDigit() }
            val isValid = initialNumber.isNotBlank() && !isError
            
            TextButton(
                onClick = {
                    if (isValid) {
                        val trimmedNum = initialNumber.trim()
                        contactRepository.saveEmergencyNumber(serviceType, trimmedNum)
                        onSaveSuccess(trimmedNum)
                        onDismiss()
                    }
                },
                enabled = isValid
            ) {
                Text(
                    text = "Save",
                    color = if (isValid) RosePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Row {
                val existingNumber = contactRepository.getEmergencyNumber(serviceType)
                if (!existingNumber.isNullOrBlank()) {
                    TextButton(
                        onClick = {
                            contactRepository.deleteEmergencyNumber(serviceType)
                            onDeleteSuccess()
                            onDismiss()
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
