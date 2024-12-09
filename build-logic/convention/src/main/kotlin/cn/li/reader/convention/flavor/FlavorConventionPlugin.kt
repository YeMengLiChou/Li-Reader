package cn.li.reader.convention.flavor

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

/**
 * 配置 flavor 的统一配置插件
 *
 * @author Grimrise 2024/9/20
 */
class FlavorConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.findByType<ApplicationExtension>()?.let {
            configureLocalTestApplication(it)
        }
        target.extensions.findByType<LibraryExtension>()?.let {
            configureLocalTestLibrary(it)
        }

        // 过滤掉 release 版本的 local_test
        target.disableBuildVariants(
            FlavorBuildVariantsEntity.from("release", FlavorConstants.FlavorDimension.DEBUG to FlavorConstants.FlavorType.LOCAL_TEST)
        )

    }

    private fun configureCommon(commonExtension: CommonExtension<*, *, *, *, *, *>) {
        // 添加 debug 的 flavor 维度
        with(commonExtension) {
            if (!flavorDimensions.contains(FlavorConstants.FlavorDimension.DEBUG)) {
                flavorDimensions += FlavorConstants.FlavorDimension.DEBUG
            }
        }
    }


    private fun configureLocalTestLibrary(extension: LibraryExtension) {
        configureCommon(extension)
        with(extension) {
            productFlavors {
                create(FlavorConstants.FlavorType.LOCAL_TEST) {
                    dimension = FlavorConstants.FlavorDimension.DEBUG
                    fixVariantsMatchingCallback()
                }

                create(FlavorConstants.FlavorType.OFFICIAL) {
                    dimension = FlavorConstants.FlavorDimension.DEBUG
                    fixVariantsMatchingCallback()
                }
            }
        }
    }

    private fun configureLocalTestApplication(extension: ApplicationExtension) {
        configureCommon(extension)
        with(extension) {
            productFlavors {
                create(FlavorConstants.FlavorType.LOCAL_TEST) {
                    dimension = FlavorConstants.FlavorDimension.DEBUG
                    applicationIdSuffix = ".localtest"
                    versionNameSuffix = "-localtest"
                    fixVariantsMatchingCallback()
                }

                create(FlavorConstants.FlavorType.OFFICIAL) {
                    dimension = FlavorConstants.FlavorDimension.DEBUG
                    applicationIdSuffix = ".official"
                    versionNameSuffix = "-official"
                    fixVariantsMatchingCallback()
                }
            }
        }
    }

    private fun ProductFlavor.fixVariantsMatchingCallback() {
//        matchingFallbacks.add("debug")
//        matchingFallbacks.add("release")
    }

}