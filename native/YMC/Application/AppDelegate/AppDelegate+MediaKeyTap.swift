//
//  AppDelegate+MediaKeyTap.swift
//  YMC
//
//  Created by Максим Карелов on 24/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import MediaKeyTap

extension AppDelegate: MediaKeyTapDelegate {
    func handle(mediaKey: MediaKey, event: KeyEvent) {
        switch mediaKey {
        case .playPause:
            if event.modifierFlags.contains(.command) {
                Player.instance.emitPressedButton(.TRACK_INFO)
            } else {
                Player.instance.emitPressedButton(.PLAY_PAUSE)
            }
        case .fastForward:
            if event.modifierFlags.contains(.command) {
                Player.instance.emitPressedButton(.LIKE)
            } else {
                Player.instance.emitPressedButton(.NEXT)
            }
        case .rewind:
            if event.modifierFlags.contains(.command) {
                Player.instance.emitPressedButton(.LINK)
            } else {
                Player.instance.emitPressedButton(.PREV)
            }
        default:
            return
        }
    }

    func readKeys() {
        if !isAccessibilityAvailable { return }

        mediaKeyTap = MediaKeyTap(delegate: self)
        mediaKeyTap?.start()
    }
}
