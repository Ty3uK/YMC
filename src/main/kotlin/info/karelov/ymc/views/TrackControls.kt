package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import info.karelov.ymc.classes.PlayerButtons
import javafx.scene.layout.Priority
import org.tinylog.Logger
import tornadofx.*

class TrackControls : View() {
    private val controller: TrackControlsController by inject()

    override val root = hbox {
        vboxConstraints {
            marginBottom = 32.0
        }

        button("Prev") {
            hboxConstraints {
                marginRight = 20.0
                hGrow = Priority.ALWAYS
            }

            action {
                controller.onButtonPress(PlayerButtons.PREV)
            }
        }

        button("Play") {
            hboxConstraints {
                marginRight = 20.0
                hGrow = Priority.ALWAYS
            }

            action {
                controller.onButtonPress(PlayerButtons.PLAY_PAUSE)
            }
        }

        button("Next") {
            hboxConstraints {
                hGrow = Priority.ALWAYS
            }

            action {
                controller.onButtonPress(PlayerButtons.NEXT)
            }
        }
    }
}

class TrackControlsController: Controller() {
    fun onButtonPress(button: PlayerButtons) {
        Player.instance.emitPressedButton(button)
    }
}
