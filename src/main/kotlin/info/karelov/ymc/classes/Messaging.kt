package info.karelov.ymc.classes

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import kotlin.reflect.KClass

data class IncomingMessage(
    @TypeFor(field = "data", adapter = IncomingMessageAdapter::class)
    val type: String,
    val data: Any?
)

open class IncomingMessageData

class IncomingMessageAdapter: TypeAdapter<IncomingMessageData> {
    override fun classFor(type: Any): KClass<out IncomingMessageData> = when (type as String) {
        "TRACK_INFO" -> CurrentTrack::class
        "CURRENT_TRACK" -> CurrentTrack::class
        "PLAYER_STATE" -> PlayerState::class
        "CONTROLS" -> Controls::class
        "PLAYING" -> IsPlaying::class
        "TOGGLE_LIKE" -> IsLiked::class
        else -> IncomingMessageData::class
    }
}