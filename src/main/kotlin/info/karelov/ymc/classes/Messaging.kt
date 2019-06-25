package info.karelov.ymc.classes

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import info.karelov.ymc.getBytes
import java.nio.charset.Charset
import kotlin.reflect.KClass

data class IncomingMessage(
    @TypeFor(field = "data", adapter = IncomingMessageAdapter::class)
    val type: String,
    val data: Any?
)

data class OutgoingMessage(
    val type: String
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

fun writeMessage(type: String) {
    val message = OutgoingMessage(type)
    val encodedMessage = Klaxon().toJsonString(message)

    System.out.write(getBytes(encodedMessage.length))
    System.out.write(encodedMessage.toByteArray(Charset.forName("UTF-8")))
    System.out.flush()
}
