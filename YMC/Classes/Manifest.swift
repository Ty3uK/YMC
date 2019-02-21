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
    case created
    case skiped
    case error
}

struct Manifest: Codable, Equatable {
    let name: String
    let description: String
    let path: String
    let type: String
    let allowed_extensions: [String]
    
    static func == (a: Manifest, b: Manifest) -> Bool {
        return (
            a.name == b.name &&
            a.description == b.description &&
            a.path == b.path &&
            a.type == b.type &&
            a.allowed_extensions.elementsEqual(b.allowed_extensions)
        )
    }
}

private let manifestsFolderPath = "\(Folder.home.path)Library/Application Support/Mozilla/NativeMessagingHosts"

private func createManifest(appPath: String) -> Manifest {
    return Manifest(
        name: "ymc",
        description: "Control Yandex.Music from any window in MacOS",
        path: appPath,
        type: "stdio",
        allowed_extensions: ["ymc@karelov.info"]
    )
}

func writeManifest() -> WriteManifestStatus {
    guard let appPath = Bundle.main.executablePath else { return .error }
    
    let manifest = createManifest(appPath: appPath)
    let encoder = JSONEncoder()
    
    encoder.outputFormatting = .prettyPrinted
    
    guard let manifestData = try? encoder.encode(manifest) else { return .error }
    guard let folder = try? Folder(path: manifestsFolderPath) else { return .error }
    
    var targetFile: File? = nil
    
    if let file = try? folder.file(named: "ymc.json") {
        guard let existingManifestData = try? file.read() else { return .error }
        guard let existingManifest = try? JSONDecoder().decode(Manifest.self, from: existingManifestData) else { return .error }
        
        if existingManifest == manifest {
            return .skiped
        }
        
        targetFile = file
    } else {
        targetFile = try? folder.createFile(named: "ymc.json")
    }
    
    if targetFile == nil {
        return .error
    }
    
    do {
        try targetFile!.write(data: manifestData)
    } catch {
        return .error
    }
    
    return .updated
}
