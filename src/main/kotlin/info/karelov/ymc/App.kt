package info.karelov.ymc

import com.beust.klaxon.Klaxon
import info.karelov.ymc.classes.CurrentTrack
import info.karelov.ymc.classes.IncomingMessage
import info.karelov.ymc.classes.Player
import io.reactivex.schedulers.Schedulers
import javafx.application.Platform
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.find
import tornadofx.launch
import tornadofx.runAsync
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class App: App() {
    var showStage = false

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)
        stage.width = 256.0
        stage.height = 448.0
        stage.isResizable = false
        stage.scene = createPrimaryScene(find<BaseView>())

        trayicon(resources.stream("/icon.png")) {
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

                        if (showStage) {
                            stage.show()
                            stage.toFront()
                        } else {
                            stage.hide()
                        }
                    }
                }
            })
        }
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

                        if (message.data is CurrentTrack) {
                            Player.instance.changeCurrentTrack(message.data)
                        }
                    }
                }
            observable.connect()

            launch<info.karelov.ymc.App>(args)
        }
    }
}
