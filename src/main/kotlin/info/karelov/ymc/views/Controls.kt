package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import info.karelov.ymc.classes.PlayerButtons
import javafx.scene.layout.Priority
import tornadofx.*

class Controls: View() {
    private val controller: ControlsController by inject()

    override val root = hbox {
        button("Share") {
            hboxConstraints {
                marginRight = 20.0
                hGrow = Priority.ALWAYS
            }

            action {
                controller.onButtonPress(PlayerButtons.LINK)
            }
        }

        button("Like") {
            hboxConstraints {
                marginRight = 20.0
                hGrow = Priority.ALWAYS
            }

            action {
                controller.onButtonPress(PlayerButtons.LIKE)
            }
        }

        button("Settings") {
            hboxConstraints {
                hGrow = Priority.ALWAYS
            }
        }
    }
}

class ControlsController: Controller() {
    fun onButtonPress(button: PlayerButtons) {
        Player.instance.emitPressedButton(button)
    }
}
