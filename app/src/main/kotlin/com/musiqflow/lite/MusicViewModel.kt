package com.musiqflow.lite

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(app: Application) : AndroidViewModel(app) {

    val searchResults = MutableStateFlow<List<SongResult>>(emptyList())
    val isSearching = MutableStateFlow(false)
    val isLoadingMore = MutableStateFlow(false)
    private var searchContinuation: String? = null
    val currentSong = MutableStateFlow<SongResult?>(null)
    val isPlaying = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)
    val progress = MutableStateFlow(0f)
    val currentPositionMs = MutableStateFlow(0L)
    val durationMs = MutableStateFlow(0L)
    val statusText = MutableStateFlow("")
    val searchHistory = MutableStateFlow<List<String>>(emptyList())
    val lyrics = MutableStateFlow<String?>(null)
    val isLoadingLyrics = MutableStateFlow(false)
    val parsedLyrics = MutableStateFlow<List<Pair<Long, String>>>(emptyList())
    val artistThumbnails = MutableStateFlow<Map<String, String>>(emptyMap())

    private val currentQueue = MutableStateFlow<List<SongResult>>(emptyList())
    private val streamCache = mutableMapOf<String, String>()
    private var controller: MediaController? = null
    private var controllerFuture: com.google.common.util.concurrent.ListenableFuture<MediaController>? = null

    fun fetchArtistThumbnail(artistName: String) {
        if (artistThumbnails.value.containsKey(artistName)) return
        viewModelScope.launch {
            val url = MusiqFlowYouTube.getArtistThumbnail(artistName) ?: return@launch
            artistThumbnails.value = artistThumbnails.value + (artistName to url)
        }
    }

    fun connectService() {
        try {
            val ctx = getApplication<Application>()
            
            // Set up callbacks for notification buttons
            MusicService.onNextRequested = { playNext() }
            MusicService.onPreviousRequested = { playPrev() }
            
            val token = SessionToken(ctx, ComponentName(ctx, MusicService::class.java))
            controllerFuture = MediaController.Builder(ctx, token).buildAsync()
            controllerFuture?.addListener({
                try {
                    controller = controllerFuture?.get()
                    controller?.addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(playing: Boolean) { 
                            isPlaying.value = playing 
                        }
                        override fun onPlaybackStateChanged(state: Int) {
                            when (state) {
                                Player.STATE_READY -> { 
                                    durationMs.value = controller?.duration ?: 0L
                                    isLoading.value = false
                                    statusText.value = "Playing ♪" 
                                }
                                Player.STATE_BUFFERING -> { 
                                    isLoading.value = true
                                    statusText.value = "Buffering..." 
                                }
                                Player.STATE_ENDED -> {
                                    // Handled in MusicService directly for screen-off support
                                    statusText.value = "Track ended"
                                }
                            }
                        }
                        override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                            // Removed: handled in MusicService to avoid double skip
                        }
                    })
                    startProgressUpdater()
                } catch (e: Exception) { e.printStackTrace() }
            }, MoreExecutors.directExecutor())
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                controller?.let { c ->
                    val pos = c.currentPosition
                    val dur = c.duration.takeIf { it > 0 } ?: 1L
                    currentPositionMs.value = pos
                    durationMs.value = dur
                    progress.value = pos.toFloat() / dur.toFloat()
                }
                kotlinx.coroutines.delay(500)
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) return
        addToHistory(query)
        viewModelScope.launch {
            isSearching.value = true
            searchResults.value = emptyList()
            searchContinuation = null
            val (results, continuation) = MusiqFlowYouTube.search(query)
            searchResults.value = results
            searchContinuation = continuation
            if (results.isNotEmpty()) currentQueue.value = results
            isSearching.value = false
        }
    }

    fun loadMoreResults() {
        val cont = searchContinuation ?: return
        if (isLoadingMore.value) return
        viewModelScope.launch {
            isLoadingMore.value = true
            val (more, nextCont) = MusiqFlowYouTube.searchContinuation(cont)
            if (more.isNotEmpty()) {
                searchResults.value = searchResults.value + more
                currentQueue.value = searchResults.value
            }
            searchContinuation = nextCont
            isLoadingMore.value = false
        }
    }

    fun clearSearch() {
        searchResults.value = emptyList()
        isSearching.value = false
    }

    fun playSong(song: SongResult) {
        currentSong.value = song
        isLoading.value = true
        statusText.value = "Loading stream..."
        lyrics.value = null
        parsedLyrics.value = emptyList()
        viewModelScope.launch {
            val streamUrl = streamCache[song.videoId] ?: run {
                val url = MusiqFlowYouTube.getStreamUrl(song.videoId)
                if (url != null) streamCache[song.videoId] = url
                url
            }
            if (streamUrl == null) { statusText.value = "Stream unavailable"; isLoading.value = false; return@launch }
            controller?.let { c -> c.setMediaItem(MediaItem.fromUri(streamUrl)); c.prepare(); c.play() }
        }
        viewModelScope.launch {
            isLoadingLyrics.value = true
            val raw = MusiqFlowYouTube.getLyrics(song.videoId, song.title, song.artist)
            lyrics.value = raw
            parsedLyrics.value = parseLyrics(raw)
            isLoadingLyrics.value = false
        }
    }

    private fun parseLyrics(raw: String?): List<Pair<Long, String>> {
        if (raw == null) return emptyList()
        val regex = Regex("""\[(\d{1,2}):(\d{2})\.(\d{2,3})\](.*)""")
        return raw.lines().mapNotNull { line ->
            val match = regex.find(line.trim()) ?: return@mapNotNull null
            val min = match.groupValues[1].toLongOrNull() ?: 0L
            val sec = match.groupValues[2].toLongOrNull() ?: 0L
            val ms = match.groupValues[3].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
            val text = match.groupValues[4].trim()
            if (text.isEmpty()) return@mapNotNull null
            (min * 60_000L + sec * 1_000L + ms) to text
        }.sortedBy { it.first }
    }

    fun playPause() { controller?.let { if (it.isPlaying) it.pause() else it.play() } }
    fun seekTo(f: Float) { controller?.seekTo((f * durationMs.value).toLong()) }
    fun seekForward() { controller?.seekTo((controller?.currentPosition ?: 0) + 10_000) }
    fun seekBack() { controller?.seekTo(maxOf(0, (controller?.currentPosition ?: 0) - 10_000)) }

    fun playNext() {
        val list = currentQueue.value
        val cur = currentSong.value ?: return
        val idx = list.indexOfFirst { it.videoId == cur.videoId }
        if (idx >= 0 && idx < list.size - 1) playSong(list[idx + 1])
    }

    fun playPrev() {
        val list = currentQueue.value
        val cur = currentSong.value ?: return
        val idx = list.indexOfFirst { it.videoId == cur.videoId }
        if (idx > 0) playSong(list[idx - 1])
    }

    private fun addToHistory(q: String) {
        val h = searchHistory.value.toMutableList()
        h.removeAll { it.equals(q, ignoreCase = true) }
        h.add(0, q)
        searchHistory.value = h.take(20)
    }

    override fun onCleared() {
        MusicService.onNextRequested = null
        MusicService.onPreviousRequested = null
        MediaController.releaseFuture(controllerFuture ?: return)
        super.onCleared()
    }
}
