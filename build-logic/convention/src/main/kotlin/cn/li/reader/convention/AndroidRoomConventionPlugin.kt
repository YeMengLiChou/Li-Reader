package cn.li.reader.convention

import androidx.room.gradle.RoomExtension
import cn.li.reader.convention.common.getPluginId
import cn.li.reader.convention.common.libs
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 *
 * Room 相关的配置
 * @author Grimrise 2024/9/19
 */
class AndroidRoomConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.getPluginId("room"))
                apply(libs.getPluginId("ksp"))
            }

            extensions.configure<KspExtension>() {
                arg("room.generateKotlin", "true")
            }

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                add("ksp", libs.findLibrary("room-compiler").get())
                add("implementation", libs.findLibrary("room-ktx").get())
                add("implementation", libs.findLibrary("room-runtime").get())

            }
        }
    }
}