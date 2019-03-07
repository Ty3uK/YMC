//
//  Logger.swift
//  YMC
//
//  Created by Максим Карелов on 21/02/2019.
//  Copyright © 2019 Maksim Karelov. All rights reserved.
//

import Foundation
import SwiftyBeaver

enum LogLevel {
    case info
    case error
    case warning
}

class Logger {
    static var instance: Logger {
        get { return _instance }
    }
    
    var enabled: Bool {
        get { return UserDefaults.standard.bool(forKey: "debug") }
    }
    
    private static let _instance = Logger()
    private var logger: SwiftyBeaver.Type
    
    private init() {
        logger = SwiftyBeaver.self
        
        let file = FileDestination()
        
        file.logFileURL = URL(fileURLWithPath: "/tmp/ymc.log")
        logger.addDestination(file)
    }
    
    func log(logLevel: LogLevel, message: Any) {
        if !enabled { return }
        
        switch logLevel {
        case .info:
            logger.info(message)
        case .warning:
            logger.warning(message)
        case .error:
            logger.error(message)
        }
    }
}
