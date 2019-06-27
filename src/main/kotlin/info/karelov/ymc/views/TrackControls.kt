package info.karelov.ymc.views

import info.karelov.ymc.classes.Player
import info.karelov.ymc.classes.PlayerButtons
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import tornadofx.*

class TrackControls : View() {
    private val controller: TrackControlsController by inject()
    private val imageSize = 28.0
    private val prevImage = ImageView(
        Image("/icons/prev.png", imageSize, imageSize, true, true)
    )
    private val playImage = ImageView(
        Image("/icons/play.png", imageSize, imageSize, true, true)
    )
    private val pauseImage = ImageView(
        Image("/icons/pause.png", imageSize, imageSize, true, true)
    )
    private val nextImage = ImageView(
        Image("/icons/next.png", imageSize, imageSize, true, true)
    )

    init {
        arrayOf(prevImage, playImage, pauseImage, nextImage).forEach {
            it.fitWidth = imageSize
            it.fitHeight = imageSize
        }
    }

    override val root = pane {
        style {
            translateY = 322.px
        }

        button("", prevImage) {
            style {
                translateX = 48.px
                background = null
                border = null
            }

            action {
                controller.onButtonPress(PlayerButtons.PREV)
            }

            disableProperty().bind(controller.isPrevEnabled.not())
        }

        button("", playImage) {
            style {
                translateX = 108.px
                background = null
                border = null
            }

            action {
                controller.onButtonPress(PlayerButtons.PLAY_PAUSE)
            }

            disableProperty().bind(controller.isNextEnabled.not())

            controller.isPlaying.addListener { _, _, newValue ->
                run {
                    runAsync { } ui {
                        this.graphic = if (newValue) {
                            pauseImage
                        } else {
                            playImage
                        }
                    }
                }
            }
        }

        button("", nextImage) {
            style {
                translateX = 162.px
                background = null
                border = null
            }

            action {
                controller.onButtonPress(PlayerButtons.NEXT)
            }
        }
    }
}

class TrackControlsController : Controller() {
    val isPrevEnabled = SimpleBooleanProperty(false)
    val isNextEnabled = SimpleBooleanProperty(false)
    val isPlaying = SimpleBooleanProperty(false)

    init {
        Player.instance.`availableControls$`.subscribe {
            isPrevEnabled.value = it.prev
            isNextEnabled.value = it.next
        }

        Player.instance.`playingState$`.subscribe {
            isPlaying.value = it
        }
    }

    fun onButtonPress(button: PlayerButtons) {
        Player.instance.emitPressedButton(button)
    }
}
