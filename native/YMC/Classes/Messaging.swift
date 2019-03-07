//
//  Messaging.swift
//  YMC
//
//  Created by Максим Карелов on 26/01/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Foundation

struct OutgoingMessage: Encodable {
    let type: String
}

struct EncodedOutgoingMessage {
    let content: String
    let length: String
}

struct IncomingMessage: Decodable {
    let type: String
    let data: Any?
    
    private enum CodingKeys: String, CodingKey {
        case type
        case data
    }
    
    private typealias IncomingMessageDataDecoder = (KeyedDecodingContainer<CodingKeys>) throws -> Any
    
    private static var decoders: [String: IncomingMessageDataDecoder] = [:]
    
    static func register<A: Decodable>(_ type: A.Type, for typeName: String) {
        decoders[typeName] = { container in
            try container.decode(A.self, forKey: .data)
        }
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        type = try container.decode(String.self, forKey: .type)
        
        if let decode = IncomingMessage.decoders[type] {
            data = try decode(container)
        } else {
            data = nil
        }
    }
}

extension FileHandle : TextOutputStream {
    public func write(_ string: String) {
        guard let data = string.data(using: .utf8) else { return }
        self.write(data)
    }
}

private func encodeMessage(_ message: OutgoingMessage) -> EncodedOutgoingMessage? {
    guard let json = try? JSONEncoder().encode(message),
        let jsonString = String(data: json, encoding: .utf8),
        let lengthString = String(data: pack("=I", [json.count], .utf8), encoding: .utf8)
        else {
            return nil
    }

    return EncodedOutgoingMessage(
        content: jsonString,
        length: lengthString
    )
}

private func writeEncodedMessage(_ message: EncodedOutgoingMessage) {
    var standardOutput = FileHandle.standardOutput
    print(message.length, separator: "", terminator: "", to: &standardOutput)
    print(message.content, separator: "", terminator: "", to: &standardOutput)
    fflush(__stdoutp)
}

func writeMessage(_ messageType: String) {
    if let encodedMessage = encodeMessage(OutgoingMessage(type: messageType)) {
        writeEncodedMessage(encodedMessage)
    }
}
