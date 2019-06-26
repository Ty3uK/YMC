package info.karelov.ymc.classes

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import info.karelov.ymc.Main
import java.io.File
import java.lang.StringBuilder
import kotlin.Exception

enum class WriteManifestStatus {
    Updated,
    Skipped,
    Error
}

data class Manifest(
    val name: String,
    val description: String,
    val path: String,
    val type: String
) {
    @Suppress("PropertyName")
    var allowed_extensions: Array<String>? = null
    @Suppress("PropertyName")
    var allowed_origins: Array<String>? = null

    override fun equals(other: Any?): Boolean {
        if (other !is Manifest) {
            return false
        }

        return (
            this.name == other.name &&
            this.description == other.description &&
            this.path == other.path &&
            this.type == other.type &&
            (
                (this.allowed_extensions != null && other.allowed_extensions != null && this.allowed_extensions!!.contentEquals(
                    other.allowed_extensions!!
                )) ||
                (this.allowed_origins != null && other.allowed_origins != null && this.allowed_origins!!.contentEquals(
                    other.allowed_origins!!
                ))
            )
        )
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (allowed_extensions?.contentHashCode() ?: 0)
        result = 31 * result + (allowed_origins?.contentHashCode() ?: 0)
        return result
    }
}

private val USER_HOME = System.getProperty("user.home")
private val paths = arrayOf(
    "$USER_HOME/.config/google-chrome",
    "$USER_HOME/.config/google-chrome-beta",
    "$USER_HOME/.config/chromium",
    "$USER_HOME/.config/opera",
    "$USER_HOME/.config/yandex-browser",
    "$USER_HOME/.config/yandex-browser-beta",
    "$USER_HOME/.mozilla"
)

private fun createManifest(appPath: String, isChromeBasedBrowser: Boolean): Manifest {
    val manifest = Manifest(
        name = "ymc",
        description = "Control Yandex.Music from any window",
        path = appPath,
        type = "stdio"
    )

    if (isChromeBasedBrowser) {
        manifest.allowed_origins = arrayOf("chrome-extension://mhjachgjcpppedbkfaajhodfgiilbmci/")
    } else {
        manifest.allowed_extensions = arrayOf("ymc@karelov.info")
    }

    return manifest
}

private fun writeManifest(path: String): WriteManifestStatus {
    val isFirefox = path.contains(".mozilla")
    val nativeMessagingDirName = if (isFirefox) {
        "native-messaging-hosts"
    } else {
        "NativeMessagingHosts"
    }
    val appPath = File(Main::class.java.protectionDomain.codeSource.location.toURI()).absolutePath ?: return WriteManifestStatus.Error
    val manifest = createManifest(appPath, !isFirefox)
    val manifestDataUgly = Klaxon().toJsonString(manifest)
    val manifestData = (Parser.default().parse(StringBuilder(manifestDataUgly)) as JsonObject).toJsonString(true)

    if (manifestData == "null") {
        return WriteManifestStatus.Error
    }

    val browserDir = File(path)
    val folder: String

    if (!browserDir.exists()) {
        return WriteManifestStatus.Skipped
    } else {
        val nativeMessagingDir = File("$path/$nativeMessagingDirName")

        folder = if (nativeMessagingDir.exists()) {
            nativeMessagingDir.absolutePath
        } else {
            try {
                nativeMessagingDir.mkdir()
            } catch (e: SecurityException) {
                return WriteManifestStatus.Skipped
            }

            nativeMessagingDir.absolutePath
        }
    }

    val targetFile = File("$folder/ymc.json")
    val writeToFile: Boolean

    writeToFile = if (targetFile.exists()) {
        try {
            val existsManifestData = targetFile.readText()

            if (existsManifestData == manifestData) {
                return WriteManifestStatus.Skipped
            }

            true
        } catch (e: Exception) {
            true
        }
    } else {
        true
    }

    if (writeToFile) {
        try {
            targetFile.writeText(manifestData)
        } catch (e: Exception) {
            return WriteManifestStatus.Error
        }
    } else {
        return WriteManifestStatus.Skipped
    }

    return WriteManifestStatus.Updated
}

fun processManifests(): WriteManifestStatus {
    val results = paths.map { writeManifest(it) }

    if (results.contains(WriteManifestStatus.Error)) {
        return WriteManifestStatus.Error
    } else if (results.contains(WriteManifestStatus.Updated)) {
        return WriteManifestStatus.Updated
    }

    return WriteManifestStatus.Skipped
}
