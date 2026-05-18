package hejulian.ai.myapplication

import hejulian.ai.myapplication.core.logging.AppLogger
import hejulian.ai.myapplication.core.logging.InMemoryAppLogger

object AppDependencies {
    val logger: AppLogger = InMemoryAppLogger()
}
