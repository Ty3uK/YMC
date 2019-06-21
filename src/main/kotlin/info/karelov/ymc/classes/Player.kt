package info.karelov.ymc.classes

import io.reactivex.subjects.BehaviorSubject
import javafx.scene.image.Image

enum class PlayerControlsAvailability {
    PREV,
    NEXT,
    LIKE,
}

enum class PlayerButtons {
    PREV,
    PLAY_PAUSE,
    NEXT,
    LINK,
    LIKE,
    TRACK_INFO,
    REFRESH,
    NOOP,
}

data class CurrentTrack(
    val title: String,
    val artist: String,
    val cover: String?,
    val liked: Boolean,
    val link: String?
)

data class Controls(
    val next: Boolean?,
    val prev: Boolean?,
    val like: Boolean?
)

data class IsPlaying(
    val state: Boolean
)

data class IsLiked(
    val state: Boolean
)

data class PlayerState(
    val currentTrack: CurrentTrack,
    val controls: Controls,
    val isPlaying: Boolean
)

@Suppress("PrivatePropertyName", "ObjectPropertyName")
class Player {
    private val `_availableControls$` = BehaviorSubject.createDefault(Controls(
        next = true,
        prev = false,
        like = false
    ))
    private val `_playingState$` = BehaviorSubject.createDefault(false)
    private val `_currentTrack$` = BehaviorSubject.createDefault(CurrentTrack(
        title = "No title",
        artist = "No artist",
        cover = null,
        liked = false,
        link = null
    ))
    private val `_buttonPress$` = BehaviorSubject.createDefault(PlayerButtons.NOOP)
    private val `_coverImage$` = BehaviorSubject.createDefault("/logo.png")

    val `availableControls$` = `_availableControls$`.hide()
    val `currentTrack$` = `_currentTrack$`.hide()
    val `playingState$` = `_playingState$`.hide()
    val `buttonPress$` = `_buttonPress$`.hide()
    val `coverImage$` = `_coverImage$`.hide()

    val currentTrack: CurrentTrack? get() = `_currentTrack$`.value
    val coverImage: String? get() = `_coverImage$`.value

    fun changeControls(controls: Controls) {
        `_availableControls$`.onNext(controls)
    }

    fun changePlayingState(state: Boolean) {
        `_playingState$`.onNext(state)
    }

    fun changeCurrentTrack(state: CurrentTrack) {
        `_currentTrack$`.onNext(state)
    }

    fun changeLikedState(state: Boolean) {
        val currentTrack = this.currentTrack ?: return
        `_currentTrack$`.onNext(CurrentTrack(
            title = currentTrack.title,
            artist= currentTrack.artist,
            cover = currentTrack.cover,
            liked = state,
            link = currentTrack.link
        ))
    }

    fun emitPressedButton(button: PlayerButtons) {
        `_buttonPress$`.onNext(button)
    }

    fun changeCoverImage(url: String?) {
        `_coverImage$`.onNext(url ?: "/logo.png")
    }

    companion object {
        val instance get() = _instance
        private val _instance = Player()
    }
}
