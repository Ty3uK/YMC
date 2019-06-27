package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import tornadofx.*

class TrackCover : View() {
    private val controller: TrackCoverController by inject()

    override val root = vbox {
        vboxConstraints {
            marginBottom = 32.0
        }

        style {
            translateX = 48.px
            translateY = 48.px
        }

        imageview {
            style {
                fitWidth = 160.0
                fitHeight = 160.0
            }

            imageProperty().bind(controller.image)
        }
    }
}

class TrackCoverController : Controller() {
    var image = SimpleObjectProperty<Image>()

    init {
        Player.instance.`coverImage$`.subscribe {
            image.value = Image(it, 160.0, 160.0, true, true)
        }
    }
}
