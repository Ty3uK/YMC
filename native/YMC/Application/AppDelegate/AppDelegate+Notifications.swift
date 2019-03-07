//
//  AppDelegate+Notifications.swift
//  YMC
//
//  Created by Максим Карелов on 24/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Foundation
import Cocoa

extension AppDelegate {
    func showTrackInfoNotification() {
        guard let currentTrack = Player.instance.currentTrack else { return }
        let notification = NSUserNotification()

        notification.title = currentTrack.artist
        notification.informativeText = currentTrack.title
        notification.contentImage = Player.instance.coverImage

        NSUserNotificationCenter.default.deliver(notification)
    }

    func copyTrackLinkToClipboard() {
        if let link = Player.instance.currentTrack?.link?.absoluteString {
            let clipboard = NSPasteboard.general
            clipboard.clearContents()
            clipboard.setString(link, forType: .string)
        }
    }
}
