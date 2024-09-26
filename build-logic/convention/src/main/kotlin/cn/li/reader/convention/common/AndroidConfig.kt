package cn.li.reader.convention.common

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun configureCommon(
    project: Project,
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    val properties = ProjectProperties.from(project)
    commonExtension.apply {
        namespace = properties.applicationId
        compileSdk = properties.compileSdk

        defaultConfig {
            minSdk = properties.minSdk
            compileSdk = properties.compileSdk
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        buildFeatures {
            buildConfig = true
            viewBinding = true
        }
    }
    if (commonExtension is ApplicationExtension) {
        configureAndroidApplication(properties, commonExtension)
    } else if (commonExtension is LibraryExtension) {
        configureAndroidLibrary(properties, commonExtension)
    }

    project.configureKotlin()

}

// 配置 Application 模块
private fun configureAndroidApplication(
    properties: ProjectProperties,
    extension: ApplicationExtension
) {
    extension.apply {
        namespace = properties.applicationId
        defaultConfig {
            targetSdk = properties.targetSdk
            compileSdk = properties.compileSdk
            versionCode = properties.versionCode
            versionName = properties.versionName
            applicationId = properties.applicationId

            buildConfigField("String", "appName", "\"${properties.applicationId}\"")
            buildConfigField("String", "appVersionName", "\"${properties.versionName}\"")
            buildConfigField("int", "appVersionCode", "${properties.versionCode}")
            buildConfigField("int", "targetSdkVersion", "${properties.targetSdk}")
            buildConfigField("int", "compileSdkVersion", "${properties.compileSdk}")
            buildConfigField("int", "minSdkVersion", "${properties.minSdk}")
        }
    }
}


// 配置 Library 模块
private fun configureAndroidLibrary(
    properties: ProjectProperties,
    extension: LibraryExtension
) {
    extension.apply {
        defaultConfig {

        }
    }
}


// 配置 Kotlin 编译选项
private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
}