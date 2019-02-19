//
//  AppDelegate.swift
//  YMC
//
//  Created by Максим Карелов on 12/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Cocoa
import MediaKeyTap
import SwiftyBeaver
import RxSwift

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {

    let standardInput = FileHandle.standardInput
    
    let statusItem = NSStatusBar.system.statusItem(withLength: NSStatusItem.variableLength)
    let popover = NSPopover()
    
    var mediaKeyTap: MediaKeyTap? = nil
    var playerViewController: PlayerViewController? = nil
    
    let log = SwiftyBeaver.self
    
    let disposeBag = DisposeBag()
    
    override init() {
        super.init()
        
        let file = FileDestination()
        
        file.logFileURL = URL(fileURLWithPath: "/tmp/ymc.log")
        
        log.addDestination(file)
    }
    
    func applicationDidFinishLaunching(_ aNotification: Notification) {
        playerViewController = PlayerViewController.freshController()
        
        IncomingMessage.register(CurrentTrack.self, for: "TRACK_INFO")
        IncomingMessage.register(CurrentTrack.self, for: "CURRENT_TRACK")
        IncomingMessage.register(Controls.self, for: "CONTROLS")
        IncomingMessage.register(IsPlaying.self, for: "PLAYING")
        IncomingMessage.register(IsLiked.self, for: "TOGGLE_LIKE")
        
        setupStatusItem()
        setupPopover()
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
            default:
                return
            }
        }.disposed(by: disposeBag)
    }

    func applicationWillTerminate(_ aNotification: Notification) {
        // Insert code here to tear down your application
    }
    
    @objc func togglePopover(_ sender: Any?) {
        if popover.isShown {
            popover.performClose(sender)
        } else {
            if let button = statusItem.button {
                NSApplication.shared.activate(ignoringOtherApps: true)
                popover.show(relativeTo: button.bounds, of: button, preferredEdge: NSRectEdge.minY)
            }
        }
    }
    
    private func setStatusItemTitle(_ title: String? = nil) {
        if let button = statusItem.button {
            button.image = NSImage(named: "logo")
            
        }
    }
    
    private func setupStatusItem(_ title: String? = nil) {
        setStatusItemTitle()
        
        if let button = statusItem.button {
            button.action = #selector(togglePopover(_:))
        }
    }
    
    private func setupPopover() {
        popover.behavior = .transient
        popover.contentViewController = playerViewController
    }
    
    private func readKeys() {
        mediaKeyTap = MediaKeyTap(delegate: self)
        mediaKeyTap?.start()
    }
    
    private func readMessages() {
        standardInput.readabilityHandler = { pipe in
            let data = pipe.availableData
            
            if data.count == 0 || data.count == 4 { return }
            
            guard let unpacked = try? unpack("=I", data.subdata(in: 0..<4)) else { return }
            guard let length = unpacked[0] as? Int else { return }
            
            var message: String
            
            if length > 4096 {
                message = String(data: data, encoding: .utf8) ?? ""
            } else {
                message = String(data: data.subdata(in: 4..<(length + 4)), encoding: .utf8) ?? ""
            }
            
            if message.count == 0 { return }
            
            guard let json = try? JSONDecoder().decode(IncomingMessage.self, from: message.data(using: .utf8)!) else { return }

            switch json.data {
            case let data as CurrentTrack:
                Player.instance.changeCoverImage(data.cover)
                Player.instance.changeCurrentTrack(data)
            case let data as Controls:
                Player.instance.changeControls(data)
            case let data as IsPlaying:
                Player.instance.changePlayingState(data.state)
            case let data as IsLiked:
                Player.instance.changeLikedState(data.state)
            default:
                return
            }
        }
    }
    
}

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
}

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

