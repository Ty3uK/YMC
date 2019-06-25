package info.karelov.ymc

import com.beust.klaxon.Klaxon
import info.karelov.ymc.classes.*
import io.reactivex.schedulers.Schedulers
import javafx.application.Platform
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.tinylog.Logger
import tornadofx.*
import tornadofx.App
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class App: App() {
    var showStage = false

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)
        stage.width = 256.0
        stage.height = 448.0
        stage.isResizable = false
        stage.scene = createPrimaryScene(find<BaseView>())

        stage.focusedProperty().onChange {
            if (!it) {
                showStage = false
                toggleStageVisibility(stage)
            }
        }

        trayicon(getResizeTrayIcon(), null) {
            this.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.button != MouseEvent.BUTTON1) {
                        return
                    }

                    Platform.runLater {
                        val screen = Screen.getPrimary()

                        val eventX = e.locationOnScreen.x.toDouble()
                        val eventY = e.locationOnScreen.y.toDouble()

                        val modY = if (eventY > screen.visualBounds.maxY / 2) {
                            this@trayicon.size.height + stage.height
                        } else {
                            0.0
                        }

                        stage.x = eventX - stage.width / 2
                        stage.y = eventY - modY

                        showStage = !showStage

                        toggleStageVisibility(stage)
                    }
                }
            })
        }
    }

    private fun toggleStageVisibility(stage: Stage) {
        if (showStage) {
            stage.show()
            stage.toFront()
        } else {
            stage.hide()
        }
    }

    private fun getResizeTrayIcon(): BufferedImage {
        val tray = SystemTray.getSystemTray()
        val size = tray.trayIconSize
        val icon = ImageIO.read(resources.url("/icon.png"))
        val scaled = icon.getScaledInstance(size.width - 2, size.height - 2, Image.SCALE_SMOOTH)
        val bufferedImage = BufferedImage(size.width - 2, size.height - 2, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage.createGraphics()

        g.drawImage(scaled, 0, 0, null)
        g.dispose()

        return bufferedImage
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val observable = getObservable()

            observable
                .observeOn(Schedulers.computation())
                .subscribe {
                    runAsync {
                        val message = Klaxon().parse<IncomingMessage>(it) ?: return@runAsync
                        val data = message.data

                        Logger.info(message)

                        when (data) {
                            is PlayerState -> {
                                Player.instance.changeCoverImage(data.currentTrack.cover)
                                Player.instance.changeCurrentTrack(data.currentTrack)
                                Player.instance.changeControls(data.controls)
                                Player.instance.changePlayingState(data.isPlaying)
                            }
                            is CurrentTrack -> {
                                Player.instance.changeCoverImage(data.cover)
                                Player.instance.changeCurrentTrack(data)
                            }
                            is Controls -> Player.instance.changeControls(data)
                            is IsPlaying -> Player.instance.changePlayingState(data.state)
                            is IsLiked -> Player.instance.changeLikedState(data.state)
                        }
                    }
                }
            observable.connect()

            Player.instance.`buttonPress$`.subscribe {
                when (it) {
                    PlayerButtons.LINK -> run {
                        val currentTrack = Player.instance.currentTrack ?: return@subscribe
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        val link = StringSelection(currentTrack.link)
                        clipboard.setContents(link, null)
                    }
                    PlayerButtons.PLAY_PAUSE -> writeMessage("PLAY_PAUSE")
                    PlayerButtons.LIKE -> writeMessage("TOGGLE_LIKE")
                    PlayerButtons.NEXT -> writeMessage("NEXT")
                    PlayerButtons.PREV -> writeMessage("PREV")
                    PlayerButtons.REFRESH -> writeMessage("REFRESH")
                    else -> return@subscribe
                }
            }

            launch<info.karelov.ymc.App>(args)
        }
    }
}
