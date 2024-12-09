package cn.li.startup.compiler

import cn.li.startup.common.annotation.Startup
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSDeclarationContainer
import com.google.devtools.ksp.symbol.KSNode
import kotlin.math.log

/**
 *
 *
 * @author Grimrise 2024/10/10
 */
class StartupTaskProcessor(
    environment: SymbolProcessorEnvironment
): SymbolProcessor {
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(Startup::class.qualifiedName.toString()).forEach {
            testKs(it)
//            (it.parent as KSDeclarationContainer).declarations.forEach {
//                logger.info("StartupTaskProcessor: ${it}")
//            }
        }
        return emptyList()
    }

    private fun testKs(node: KSNode) {
        if (node !is KSDeclarationContainer) {
            return
        }
        if (node is KSDeclaration) {
            logger.info("StartupTaskProcessor: {${node.containingFile}}")
            node.annotations.forEach {
                logger.info("annotations: ${it.annotationType.element?.typeArguments}")
//                logger.info("annotations: ${it.annotationType.resolve()} ${it.annotationType.resolve()::class.simpleName}")
//                it.annotationType.resolve()  .forEach {
//                    logger.info("StartupTaskProcessor: [${it.parent}]-${it}-${it::class.simpleName}")
//                }
            }
        }
        logger.info("StartupTaskProcessor: [${node.parent}]${node}-${node::class.simpleName}")
        node.declarations.forEach {
            logger.info("StartupTaskProcessor: [${it.parent}]-${it}-${it::class.simpleName}")
            testKs(it)
        }
    }
}