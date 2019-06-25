package info.karelov.ymc

import info.karelov.ymc.views.Controls
import info.karelov.ymc.views.TrackControls
import info.karelov.ymc.views.TrackCover
import info.karelov.ymc.views.TrackInfo
import tornadofx.*

class BaseView: View() {
    private val trackCover: TrackCover by inject()
    private val trackInfo: TrackInfo by inject()
    private val trackControls: TrackControls by inject()
    private val controls: Controls by inject()

    override val root = pane {
        style {
            backgroundColor += c(50, 50, 50)
        }

        this += trackCover
        this += trackInfo
        this += trackControls
        this += controls
    }
}