package cn.li.reader.convention.flavor

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

/**
 * BuildVariant 信息
 *
 * @author Grimrise 2024/9/20
 */
class FlavorBuildVariantsEntity(
    val flavors: List<Pair<String, String>>,
    val buildType: String = ALL
) {
    companion object {
        const val ALL = "*"

        fun from(
            buildType: String = ALL,
            vararg flavors: Pair<String, String>
        ): FlavorBuildVariantsEntity {
            return FlavorBuildVariantsEntity(flavors = flavors.toList(), buildType = buildType)
        }
    }
}

/**
 * 关闭对应 Flavor 的变体构建
 * @param buildVariants Pair 第一个是 FlavorDimension 的名称，第二个是 Flavor 的名称
 * */
fun Project.disableBuildVariants(
    vararg buildVariants: FlavorBuildVariantsEntity
) {
    if (buildVariants.isEmpty()) return
    val androidComponents = project.extensions.findByType<AndroidComponentsExtension<*, *, *>>()
    androidComponents?.beforeVariants { variantBuilder ->
        buildVariants.forEach { variant ->
            if ((variantBuilder.buildType == FlavorBuildVariantsEntity.ALL || variantBuilder.buildType == variant.buildType)
                && variantBuilder.productFlavors.containsAll(variant.flavors)) {
                variantBuilder.enable = false
            }
        }
    }
}
