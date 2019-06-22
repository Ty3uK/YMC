package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TrackCover: View() {
    private val controller: TrackCoverController by inject()

    override val root = vbox {
        vboxConstraints {
            marginBottom = 32.0
        }

        style {
            padding = box(0.px, 48.px)
        }

        imageview(controller.imageUrl) {
            style {
                fitWidth = 160.0
                fitHeight = 160.0
            }
        }
    }
}

class TrackCoverController: Controller() {
    var imageUrl = SimpleStringProperty()

    init {
        Player.instance.`coverImage$`.subscribe {
            imageUrl.value = it
        }
    }
}
