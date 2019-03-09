//
//  AppDelegate+Setup.swift
//  YMC
//
//  Created by Максим Карелов on 24/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Foundation
import Cocoa

extension AppDelegate {
    func prepareLaunch() {
        let options = [kAXTrustedCheckOptionPrompt.takeRetainedValue() as String: true] as CFDictionary

        if !AXIsProcessTrustedWithOptions(options) {
            showAlert(
                title: "Action required",
                message: "To be able to switch songs by pressing media keys you must activate the app in accessibility settings",
                quitAfter: false
            )
        } else {
            isAccessibilityAvailable = true
        }

        let writeManifestStatus = processManifests()

        switch writeManifestStatus {
        case .error:
            showAlert(
                title: "Error",
                message: "Cannot write manifest file for extension.\nApp will be closed.",
                quitAfter: true
            )
        case .updated:
            showAlert(
                title: "Action required",
                message: "Manifest updated. Please, restart your browser.",
                quitAfter: true
            )
        default:
            return
        }
    }

    private func showAlert(title: String, message: String, quitAfter: Bool) {
        let alert = NSAlert()

        alert.messageText = title
        alert.informativeText = message
        alert.addButton(withTitle: "Got it")

        if quitAfter && alert.runModal() == .alertFirstButtonReturn {
            exit(1)
        }
    }
}
