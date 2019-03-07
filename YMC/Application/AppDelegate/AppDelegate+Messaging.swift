//
//  AppDelegate+Messaging.swift
//  YMC
//
//  Created by Максим Карелов on 24/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Foundation

extension AppDelegate {
    func setupIncomingMessage() {
        IncomingMessage.register(CurrentTrack.self, for: "TRACK_INFO")
        IncomingMessage.register(CurrentTrack.self, for: "CURRENT_TRACK")
        IncomingMessage.register(PlayerState.self, for: "PLAYER_STATE")
        IncomingMessage.register(Controls.self, for: "CONTROLS")
        IncomingMessage.register(IsPlaying.self, for: "PLAYING")
        IncomingMessage.register(IsLiked.self, for: "TOGGLE_LIKE")
    }

    func setupRefreshInterval() {
        Timer.scheduledTimer(withTimeInterval: 30, repeats: true, block: { _ in
            writeMessage("REFRESH")
        })
    }

    func readMessages() {
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

            Logger.instance.log(logLevel: .info, message: "MESSAGE FROM EXT: \(message)")

            if message.count == 0 { return }

            guard let json = try? JSONDecoder().decode(IncomingMessage.self, from: message.data(using: .utf8)!) else { return }

            switch json.data {
            case let data as PlayerState:
                Player.instance.changeCoverImage(data.currentTrack.cover)
                Player.instance.changeCurrentTrack(data.currentTrack)
                Player.instance.changeControls(data.controls)
                Player.instance.changePlayingState(data.isPlaying)
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
