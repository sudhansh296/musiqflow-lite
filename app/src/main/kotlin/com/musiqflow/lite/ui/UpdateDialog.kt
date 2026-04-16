package com.musiqflow.lite.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.musiqflow.lite.UpdateChecker
import com.musiqflow.lite.UpdateInfo

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val updateChecker = remember { UpdateChecker(context) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🎵 Update Available!")
        },
        text = {
            Text(
                "New version ${updateInfo.version} is available!\n\n" +
                "What's new:\n${updateInfo.changelog}"
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    updateChecker.downloadUpdate(updateInfo.downloadUrl)
                    onDismiss()
                }
            ) {
                Text("Download Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        }
    )
}