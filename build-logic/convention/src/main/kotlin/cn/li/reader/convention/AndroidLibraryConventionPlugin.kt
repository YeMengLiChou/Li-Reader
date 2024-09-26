package cn.li.reader.convention

import cn.li.reader.convention.common.configureCommon
import cn.li.reader.convention.common.getPluginId
import cn.li.reader.convention.common.libs
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
           with(pluginManager) {
               apply(libs.getPluginId("android-library"))
               apply(libs.getPluginId("kotlin-android"))
               apply(libs.getPluginId("ksp"))
           }

            configureCommon(target, extensions.getByType<LibraryExtension>())
        }
    }
}