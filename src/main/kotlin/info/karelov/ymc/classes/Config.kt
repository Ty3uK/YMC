package info.karelov.ymc.classes

import com.beust.klaxon.Klaxon
import info.karelov.ymc.Main
import java.io.File

data class ConfigFile(
    val log: Boolean
)

@Suppress("ObjectPropertyName")
class Config {
    var data: ConfigFile? = null

    init {
        val dir = File(Main::class.java.protectionDomain.codeSource.location.toURI()).parent
        val configFile = File("$dir/config.json")

        if (configFile.exists()) {
            data = Klaxon().parse<ConfigFile>(configFile)
        }
    }

    companion object {
        val instance get() = _instance
        private val _instance = Config()
    }
}
