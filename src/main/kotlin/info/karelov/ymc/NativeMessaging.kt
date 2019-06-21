package info.karelov.ymc

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import java.io.InputStream
import java.nio.charset.Charset

fun readMessage(stream: InputStream): String? {
    var b = ByteArray(4)
    stream.read(b)

    val size = getInt(b)

    if (size == 0) {
        return null
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
    return Observable.create(ObservableOnSubscribe<String> {
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
}

