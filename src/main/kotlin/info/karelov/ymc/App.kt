package info.karelov.ymc

import info.karelov.ymc.classes.Player
import io.reactivex.schedulers.Schedulers
import javafx.application.Platform
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.tinylog.Logger
import tornadofx.App
import tornadofx.find
import tornadofx.launch
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
                    Logger.info(it as Any)
                }

            observable.connect()

            Player.instance.`buttonPress$`.subscribe {
                Logger.info(it)
            }

            launch<info.karelov.ymc.App>(args)
        }
    }
}
