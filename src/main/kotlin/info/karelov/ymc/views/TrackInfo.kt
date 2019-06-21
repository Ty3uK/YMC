package info.karelov.ymc.views

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

class TrackInfo : View() {
    override val root = vbox {
        vboxConstraints {
            marginBottom = 32.0
            vGrow = Priority.ALWAYS
        }

        label("No title") {
            maxWidth = Double.MAX_VALUE

            style {
                paddingBottom = 2
                fontWeight = FontWeight.EXTRA_BOLD
                fontSize = 14.px
                textFill = c("white")
                alignment = Pos.CENTER
            }
        }

        label("No artist") {
            maxWidth = Double.MAX_VALUE

            style {
                fontSize = 13.px
                textFill = c(255, 45, 85)
                alignment = Pos.CENTER
            }
        }
    }
}