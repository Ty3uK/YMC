//
//  Player.swift
//  YMC
//
//  Created by Максим Карелов on 15/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Cocoa
import Foundation
import RxSwift

enum PlayerControlsAvailability {
    case PREV
    case NEXT
    case LIKE
}

enum PlayerButtons {
    case PREV
    case PLAY_PAUSE
    case NEXT
    case LINK
    case LIKE
    case TRACK_INFO
    case REFRESH
    case NOOP
}

struct CurrentTrack: Decodable {
    let title: String
    let artist: String
    let cover: URL?
    let liked: Bool
    let link: URL?
}

struct Controls: Decodable {
    let next: Bool?
    let prev: Bool?
    let like: Bool?
}

struct IsPlaying: Decodable {
    let state: Bool
}

struct IsLiked: Decodable {
    let state: Bool
}

struct PlayerState: Decodable {
    let currentTrack: CurrentTrack
    let controls: Controls
    let isPlaying: Bool
}

class Player {
    private static let _instance = Player()

    static var instance: Player {
        get { return _instance }
    }

    private let _availableControls$ = BehaviorSubject(value: Controls(
        next: true,
        prev: false,
        like: false
    ))
    private let _playingState$ = BehaviorSubject(value: false)
    private let _currentTrack$ = BehaviorSubject(value: CurrentTrack(
        title: "No title",
        artist: "No artist",
        cover: nil,
        liked: false,
        link: nil
    ))
    private let _buttonPress$ = BehaviorSubject(value: PlayerButtons.NOOP)
    private let _coverImage$ = BehaviorSubject(value: NSImage(named: "logo_large"))

    public var availableControls$: Observable<Controls>
    public var playingState$: Observable<Bool>
    public var currentTrack$: Observable<CurrentTrack>
    public var buttonPress$: Observable<PlayerButtons>
    public var coverImage$: Observable<NSImage?>

    public var currentTrack: CurrentTrack? {
        get {
            guard let result = try? _currentTrack$.value() else { return nil }
            return result
        }
    }
    public var coverImage: NSImage? {
        get {
            guard let result = try? _coverImage$.value() else { return nil }
            return result
        }
    }

    private init() {
        availableControls$ = _availableControls$.asObservable()
        playingState$ = _playingState$.asObservable()
        currentTrack$ = _currentTrack$.asObservable()
        buttonPress$ = _buttonPress$.asObservable()
        coverImage$ = _coverImage$.asObservable()
    }

    public func changeControls(_ controls: Controls) {
        _availableControls$.onNext(controls)
    }

    public func changePlayingState(_ state: Bool) {
        _playingState$.onNext(state)
    }

    public func changeCurrentTrack(_ state: CurrentTrack) {
        _currentTrack$.onNext(state)
    }

    public func changeLikedState(_ state: Bool) {
        guard let currentTrack = try? _currentTrack$.value() else { return }

        let newCurrentTrack = CurrentTrack(
            title: currentTrack.title,
            artist: currentTrack.artist,
            cover: currentTrack.cover,
            liked: state,
            link: currentTrack.link
        )

        _currentTrack$.onNext(newCurrentTrack)
    }

    public func emitPressedButton(_ button: PlayerButtons) {
        _buttonPress$.onNext(button)
    }

    public func changeCoverImage(_ url: URL?) {
        guard let url = url else {
            _coverImage$.onNext(NSImage(named: "logo_large"))
            return
        }

        URLSession.shared.dataTask(with: url, completionHandler: { data, response, error in
            guard let data = data, error == nil else { return }
            self._coverImage$.onNext(NSImage(data: data))
        }).resume()
    }
}
