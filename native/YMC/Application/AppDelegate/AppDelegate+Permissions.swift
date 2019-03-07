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
            showAlertAndQuit(
                title: "Action required",
                message: "To be able to switch songs by pressing media keys you must activate the app in accessibility settings"
            )
        }

        let writeManifestStatus = processManifests()

        switch writeManifestStatus {
        case .error:
            showAlertAndQuit(
                title: "Error",
                message: "Cannot write manifest file for extension.\nApp will be closed."
            )
        case .updated:
            showAlertAndQuit(
                title: "Action required",
                message: "Manifest updated. Please, restart your browser."
            )
        default:
            return
        }
    }

    private func showAlertAndQuit(title: String, message: String) {
        let alert = NSAlert()

        alert.messageText = title
        alert.informativeText = message
        alert.addButton(withTitle: "Got it")

        if alert.runModal() == .alertFirstButtonReturn {
            exit(1)
        }
    }
}
