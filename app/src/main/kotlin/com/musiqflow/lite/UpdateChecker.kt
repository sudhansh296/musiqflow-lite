package com.musiqflow.lite

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class UpdateChecker(private val context: Context) {
    
    private val client = OkHttpClient()
    private val updateUrl = "https://your-portfolio-site.com/api/version.json"
    
    suspend fun checkForUpdate(): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(updateUrl)
                    .build()
                
                val response = client.newCall(request).execute()
                val jsonString = response.body?.string() ?: return@withContext null
                
                val json = JSONObject(jsonString)
                val latestVersion = json.getString("version")
                val downloadUrl = json.getString("downloadUrl")
                val changelog = json.getString("changelog")
                
                val currentVersion = getCurrentVersion()
                
                if (isNewerVersion(currentVersion, latestVersion)) {
                    UpdateInfo(latestVersion, downloadUrl, changelog)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun getCurrentVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
    
    private fun isNewerVersion(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        
        for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
            val currentPart = currentParts.getOrNull(i) ?: 0
            val latestPart = latestParts.getOrNull(i) ?: 0
            
            when {
                latestPart > currentPart -> return true
                latestPart < currentPart -> return false
            }
        }
        return false
    }
    
    fun downloadUpdate(downloadUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}

data class UpdateInfo(
    val version: String,
    val downloadUrl: String,
    val changelog: String
)