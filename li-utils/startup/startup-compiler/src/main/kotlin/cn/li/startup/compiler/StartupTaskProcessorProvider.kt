package cn.li.startup.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 *
 *
 * @author Grimrise 2024/10/10
 */
class StartupTaskProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        environment.logger.info("========== StartupTaskProcessorProvider ==========")
        return StartupTaskProcessor(environment)
    }
}