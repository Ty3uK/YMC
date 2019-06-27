package info.karelov.ymc.classes

@Suppress("ObjectPropertyName")
class Logger {
    fun log(message: Any) {
        if (Config.instance.data?.log != true) {
            return
        }

        org.tinylog.Logger.info(message)
    }

    companion object {
        val instance get() = _instance
        private val _instance = Logger()
    }
}