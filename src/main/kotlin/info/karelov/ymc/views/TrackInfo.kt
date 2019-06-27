package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class TrackInfo : View() {
    private val controller: TrackInfoController by inject()

    override val root = vbox {
        vboxConstraints {
            marginBottom = 32.0
            vGrow = Priority.ALWAYS
        }

        style {
            translateY = 256.px
            padding = box(0.px, 24.px)
        }

        text(controller.title) {
            maxWidth = Double.MAX_VALUE
            wrappingWidth = 208.0

            style {
                paddingBottom = 2
                font = Font.font("", FontWeight.EXTRA_BOLD, 12.0)
                fill = c("white")
                textAlignment = TextAlignment.CENTER
            }
        }

        text(controller.artist) {
            maxWidth = Double.MAX_VALUE
            wrappingWidth = 208.0

            style {
                font = Font(11.0)
                fill = c(255, 45, 85)
                textAlignment = TextAlignment.CENTER
            }
        }
    }
}

class TrackInfoController : Controller() {
    val title = SimpleStringProperty()
    val artist = SimpleStringProperty()

    init {
        Player.instance.`currentTrack$`.subscribe {
            title.value = if (it.title.length > 38) {
                it.title.substring(0, 35) + "..."
            } else {
                it.title
            }

            artist.value = if (it.artist.length > 38) {
                it.artist.substring(0, 35) + "..."
            } else {
                it.artist
            }
        }
    }
}
