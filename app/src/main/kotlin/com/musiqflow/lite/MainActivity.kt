package com.musiqflow.lite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.ImageLoader
import com.musiqflow.lite.ui.MusicScreen
import com.musiqflow.lite.ui.UpdateDialog
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()
    private val updateChecker by lazy { UpdateChecker(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Setup Coil with permissive HTTP client
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .crossfade(true)
                .build()
        )

        setContent {
            var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
            
            // Check for updates on app start
            LaunchedEffect(Unit) {
                updateInfo = updateChecker.checkForUpdate()
            }
            
            MusicScreen(viewModel = viewModel)
            
            // Show update dialog if available
            updateInfo?.let { info ->
                UpdateDialog(
                    updateInfo = info,
                    onDismiss = { updateInfo = null }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try { viewModel.connectService() } catch (e: Exception) { e.printStackTrace() }
    }
}
