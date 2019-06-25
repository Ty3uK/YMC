package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import info.karelov.ymc.classes.PlayerButtons
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.*
import kotlin.system.exitProcess

class Controls: View() {
    private val controller: ControlsController by inject()
    private val imageSize = 18.0
    private val linkImage = ImageView(
        Image("/icons/link.png", imageSize, imageSize, true, true)
    )
    private val likeImage = ImageView(
        Image("/icons/like.png", imageSize, imageSize, true, true)
    )
    private val likedImage = ImageView(
        Image("/icons/liked.png", imageSize, imageSize, true, true)
    )
    private val settingsImage = ImageView(
        Image("/icons/settings.png", imageSize, imageSize, true, true)
    )

    init {
        arrayOf(linkImage, likeImage, likedImage, settingsImage).forEach {
            it.fitWidth = imageSize
            it.fitHeight = imageSize
        }
    }

    override val root = pane {
        style {
            translateY = 382.px
        }

        button("", linkImage) {
            layoutX = 53.0

            style {
                background = null
                border = null
            }

            action {
                controller.onButtonPress(PlayerButtons.LINK)
            }
        }

        button("", likeImage) {
            translateX = 112.0

            style {
                background = null
                border = null
            }

            action {
                controller.onButtonPress(PlayerButtons.LIKE)
            }

            disableProperty().bind(controller.isLikeEnabled.not())

            controller.isLiked.addListener { _, _, newValue ->  run {
                runAsync {  } ui {
                    this.graphic = if (newValue) {
                        likedImage
                    } else {
                        likeImage
                    }
                }
            }}
        }

        button("", settingsImage) {
            translateX = 165.0

            style {
                background = null
                border = null
            }

            action {

            }

            contextmenu {
                item("Enable debugging")
                separator()
                item("Quit") {
                    action { exitProcess(0) }
                }
            }
        }
    }
}

class ControlsController: Controller() {
    val isLikeEnabled = SimpleBooleanProperty(false)
    val isLiked = SimpleBooleanProperty(false)

    init {
        Player.instance.`availableControls$`.subscribe {
            isLikeEnabled.value = it.like
        }

        Player.instance.`currentTrack$`.subscribe {
            isLiked.value = it?.liked ?: false
        }
    }

    fun onButtonPress(button: PlayerButtons) {
        Player.instance.emitPressedButton(button)
    }
}
