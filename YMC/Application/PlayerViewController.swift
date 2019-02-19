//
//  PlayerViewController.swift
//  YMC
//
//  Created by Максим Карелов on 12/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Cocoa
import RxSwift
import SwiftyBeaver

class PlayerViewController: NSViewController {

    @IBOutlet var songCover: NSImageView!
    @IBOutlet var songTitle: NSTextField!
    @IBOutlet var songArtist: NSTextField!
    
    @IBOutlet var rewindButton: NSButton!
    @IBOutlet var playPauseButton: NSButton!
    @IBOutlet var forwardButton: NSButton!
    
    @IBOutlet var linkButton: NSButton!
    @IBOutlet var likeButton: NSButton!
    
    private let disposeBag = DisposeBag()
    
    private var invisibleWindow: NSWindow? = nil
    
    let log = SwiftyBeaver.self
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        
        let file = FileDestination()
        
        file.logFileURL = URL(fileURLWithPath: "/tmp/ymc.log")
        
        log.addDestination(file)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        linkButton.sendAction(on: .leftMouseDown)
        
        Player.instance.currentTrack$.subscribe { event in
            guard let currentTrack = event.element else { return }
            
            DispatchQueue.main.async {
                self.songTitle.stringValue = currentTrack.title
                self.songArtist.stringValue = currentTrack.artist
                self.likeButton.state = currentTrack.liked ? .on : .off
            }
        }.disposed(by: disposeBag)
        
        Player.instance.coverImage$.subscribe { event in
            guard let coverImage = event.element else { return }
            
            DispatchQueue.main.async {
                self.songCover.image = coverImage
            }
        }.disposed(by: disposeBag)
        
        Player.instance.playingState$.subscribe { event in
            guard let playingState = event.element else { return }
            let image = NSImage(named: (playingState ? "pause" : "play"))
            
            DispatchQueue.main.async {
                self.playPauseButton.image = image
            }
        }.disposed(by: disposeBag)
        
        Player.instance.availableControls$.subscribe { event in
            guard let controls = event.element else { return }
            
            DispatchQueue.main.async {
                self.likeButton.isEnabled = controls.like ?? false
                self.rewindButton.isEnabled = controls.prev ?? false
                self.forwardButton.isEnabled = controls.next ?? false
            }
        }.disposed(by: disposeBag)
    }
    
    @IBAction func buttonClick(_ sender: NSButton) {
        guard let identifier = sender.identifier?.rawValue else { return }
        
        switch identifier {
        case "PLAY_PAUSE":
            Player.instance.emitPressedButton(.PLAY_PAUSE)
        case "LIKE":
            Player.instance.emitPressedButton(.LIKE)
        case "NEXT":
            Player.instance.emitPressedButton(.NEXT)
        case "PREV":
            Player.instance.emitPressedButton(.PREV)
        default:
            return
        }
    }
    
    @IBAction func shareButtonClick(_ sender: NSButton) {
        guard let url = Player.instance.currentTrack?.link else { return }
        
        // Create a window
        invisibleWindow = NSWindow(contentRect: NSMakeRect(0, 0, 20, 5), styleMask: .borderless, backing: .buffered, defer: false)
        invisibleWindow!.backgroundColor = .red
        invisibleWindow!.alphaValue = 0
        
        // find the coordinates of the statusBarItem in screen space
        let buttonRect: NSRect = sender.convert(sender.bounds, to: nil)
        let screenRect: NSRect = sender.window!.convertToScreen(buttonRect)
        
        // calculate the bottom center position (10 is the half of the window width)
        let posX = screenRect.origin.x + (screenRect.width / 2) - 10
        let posY = screenRect.origin.y
        
        // position and show the window
        invisibleWindow!.setFrameOrigin(NSPoint(x: posX, y: posY))
        invisibleWindow!.makeKeyAndOrderFront(self)
        
        guard let view = invisibleWindow!.contentView else { return }
        
        let picker = NSSharingServicePicker(items: [url.absoluteString])
        
        picker.delegate = self
        picker.show(relativeTo: view.frame, of: view, preferredEdge: .minY)
    }
    
}

extension PlayerViewController {
    static func freshController() -> PlayerViewController {
        let storyboard = NSStoryboard(name: NSStoryboard.Name("Main"), bundle: nil)
        let identifier = NSStoryboard.SceneIdentifier("PlayerViewController")
        
        guard let viewController = storyboard.instantiateController(withIdentifier: identifier) as? PlayerViewController else {
            fatalError("Cannot find PlayerViewController")
        }
        
        return viewController
    }
}

extension PlayerViewController: NSSharingServicePickerDelegate {
    func sharingServicePicker(_ sharingServicePicker: NSSharingServicePicker, didChoose service: NSSharingService?) {
        invisibleWindow?.close()
    }
}
