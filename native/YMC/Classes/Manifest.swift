//
//  Manifest.swift
//  YMC
//
//  Created by Максим Карелов on 20/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Foundation
import Files

enum WriteManifestStatus {
    case updated
    case skipped
    case error
}

struct Manifest: Codable, Equatable {
    let name: String
    let description: String
    let path: String
    let type: String
    var allowed_extensions: [String]? = nil
    var allowed_origins: [String]? = nil

    init(name: String, description: String, path: String, type: String) {
        self.name = name
        self.description = description
        self.path = path
        self.type = type
    }

    static func == (a: Manifest, b: Manifest) -> Bool {
        return (
            a.name == b.name &&
            a.description == b.description &&
            a.path == b.path &&
            a.type == b.type &&
            (
                (a.allowed_extensions != nil && b.allowed_extensions != nil && a.allowed_extensions!.elementsEqual(b.allowed_extensions!)) ||
                (a.allowed_origins != nil && b.allowed_origins != nil && a.allowed_origins!.elementsEqual(b.allowed_origins!))
            )
        )
    }
}

private let paths = [
    "\(Folder.home.path)Library/Application Support/Mozilla",
    "\(Folder.home.path)Library/Application Support/Google/Chrome",
    "\(Folder.home.path)Library/Application Support/Chromium",
    "\(Folder.home.path)Library/Application Support/com.operasoftware.Opera",
    "\(Folder.home.path)Library/Application Support/Yandex/YandexBrowser",
    "\(Folder.home.path)Library/Application Support/Vivaldi"
]

private func createManifest(appPath: String, isChromeBasedBrowser: Bool) -> Manifest {
    var manifest = Manifest(
        name: "ymc",
        description: "Control Yandex.Music from any window in MacOS",
        path: appPath,
        type: "stdio"
    )

    if isChromeBasedBrowser {
        manifest.allowed_origins = ["chrome-extension://mhjachgjcpppedbkfaajhodfgiilbmci/"]
    } else {
        manifest.allowed_extensions = ["ymc@karelov.info"]
    }

    return manifest
}

private func writeManifest(_ path: String) -> WriteManifestStatus {
    guard let appPath = Bundle.main.executablePath else { return .error }

    let manifest = createManifest(appPath: appPath, isChromeBasedBrowser: !path.contains("Mozilla"))
    let encoder = JSONEncoder()

    encoder.outputFormatting = .prettyPrinted

    guard let manifestData = try? encoder.encode(manifest) else { return .error }
    guard let manifestDataString = String(data: manifestData, encoding: .utf8)?
        .replacingOccurrences(of: "\\/", with: "/")
        .replacingOccurrences(of: " : ", with: ": ") else { return .error }
    guard let manifestDataFixed = manifestDataString.data(using: .utf8) else { return .error }
    guard let browserFolder = try? Folder(path: path) else { return .skipped }

    var folder: Folder

    if let targetFolder = try? browserFolder.subfolder(named: "NativeMessagingHosts") {
        folder = targetFolder
    } else if let targetFolder = try? browserFolder.createSubfolder(named: "NativeMessagingHosts") {
        folder = targetFolder
    } else {
        return .skipped
    }

    var targetFile: File?

    if let file = try? folder.file(named: "ymc.json") {
        guard let existingManifestData = try? file.read() else { return .error }

        if let existingManifest = try? JSONDecoder().decode(Manifest.self, from: existingManifestData), existingManifest == manifest {
            return .skipped
        }

        targetFile = file
    } else {
        targetFile = try? folder.createFile(named: "ymc.json")
    }

    if targetFile == nil {
        return .error
    }

    do {
        try targetFile!.write(data: manifestDataFixed)
    } catch {
        return .error
    }

    return .updated
}

func processManifests() -> WriteManifestStatus {
    let results = paths.map { writeManifest($0) }

    if results.contains(.error) {
        return .error
    } else if results.contains(.updated) {
        return .updated
    }

    return .skipped
}
