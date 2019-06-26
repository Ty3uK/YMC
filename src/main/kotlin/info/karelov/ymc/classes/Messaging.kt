package info.karelov.ymc.classes

import com.beust.klaxon.Klaxon
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import java.io.InputStream
import java.io.InterruptedIOException
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.system.exitProcess

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

fun readMessage(stream: InputStream): String? {
    var b = ByteArray(4)
    stream.read(b)

    val size = getInt(b)

    if (size == 0) {
        throw InterruptedIOException("Blocked communication")
    }

    b = ByteArray(size)
    stream.read(b)

    return String(b, Charset.forName("UTF-8"))
}

fun getBytes(length: Int): ByteArray {
    val bytes = ByteArray(4)
    bytes[0] = (length and 0xFF).toByte()
    bytes[1] = (length shr 8 and 0xFF).toByte()
    bytes[2] = (length shr 16 and 0xFF).toByte()
    bytes[3] = (length shr 24 and 0xFF).toByte()
    return bytes
}

fun getInt(bytes: ByteArray): Int {
    return (bytes[3].toInt() shl 24 and -0x1000000 or (bytes[2].toInt() shl 16 and 0x00ff0000)
            or (bytes[1].toInt() shl 8 and 0x0000ff00) or (bytes[0].toInt() shl 0 and 0x000000ff))
}

fun getObservable(): ConnectableObservable<String> {
    val observable = Observable.create(ObservableOnSubscribe<String> {
        try {
            while (true) {
                val message = readMessage(System.`in`)

                if (message != null) {
                    it.onNext(message)
                }
            }
        } catch (e: Exception) {
            it.onError(e)
        }

        it.onComplete()
    }).subscribeOn(Schedulers.io()).publish()

    observable.subscribe(
        {},
        { exitProcess(0) },
        { exitProcess(0) }
    )

    return observable
}
