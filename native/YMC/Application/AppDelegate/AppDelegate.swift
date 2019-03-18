//
//  AppDelegate.swift
//  YMC
//
//  Created by Максим Карелов on 12/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Cocoa
import MediaKeyTap
import RxSwift

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {
    let standardInput = FileHandle.standardInput

    let statusItem = NSStatusBar.system.statusItem(withLength: NSStatusItem.variableLength)
    let popover = NSPopover()

    var playerViewController: PlayerViewController? = nil
    var mediaKeyTap: MediaKeyTap? = nil
    var isAccessibilityAvailable = false

    let disposeBag = DisposeBag()

    override init() {
        super.init()
    }

    func applicationDidFinishLaunching(_ aNotification: Notification) {
        prepareLaunch()

        playerViewController = PlayerViewController.freshController()

        setupIncomingMessage()
        setupStatusItem()
        setupPopover()
        setupRefreshInterval()
        readKeys()
        readMessages()

        Player.instance.buttonPress$.subscribe { event in
            guard let button = event.element else { return }

            switch button {
            case .TRACK_INFO:
                self.showTrackInfoNotification()
            case .LINK:
                self.copyTrackLinkToClipboard()
            case .PLAY_PAUSE:
                writeMessage("PLAY_PAUSE")
            case .LIKE:
                writeMessage("TOGGLE_LIKE")
            case .NEXT:
                writeMessage("NEXT")
            case .PREV:
                writeMessage("PREV")
            case .REFRESH:
                writeMessage("REFRESH")
            default:
                return
            }
        }.disposed(by: disposeBag)

        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            writeMessage("REFRESH")
        }
    }

    func applicationWillTerminate(_ aNotification: Notification) {
        // Insert code here to tear down your application
    }

    @objc func togglePopover(_ sender: NSButton) {
        if popover.isShown {
            popover.performClose(sender)
        } else {
            if let button = statusItem.button {
                NSApplication.shared.activate(ignoringOtherApps: true)
                popover.show(relativeTo: button.bounds, of: button, preferredEdge: NSRectEdge.minY)
            }
        }
    }

    private func setupStatusItem(_ title: String? = nil) {
        if let button = statusItem.button {
            button.image = NSImage(named: "logo")
            button.action = #selector(togglePopover(_:))
            button.sendAction(on: [.leftMouseDown, .rightMouseDown])
        }
    }

    private func setupPopover() {
        popover.behavior = .transient
        popover.contentViewController = playerViewController
    }
}
