package cn.li.reader.convention.common

import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

data class ProjectProperties(
    val targetSdk: Int,
    val compileSdk: Int,
    val minSdk: Int,
    val applicationId: String,
    val versionCode: Int,
    val versionName: String,
) {

    companion object {
        fun from(project: Project): ProjectProperties {
            val TARGET_SDK_VERSION: String by project
            val COMPILE_SDK_VERSION: String by project
            val MIN_SDK_VERSION: String by project
            val APPLICATION_ID: String by project
            val VERSION_CODE: String by project
            val VERSION_NAME: String by project
            return ProjectProperties(
                targetSdk = TARGET_SDK_VERSION.toInt(),
                compileSdk = COMPILE_SDK_VERSION.toInt(),
                minSdk = MIN_SDK_VERSION.toInt(),
                applicationId = APPLICATION_ID,
                versionCode = VERSION_CODE.toInt(),
                versionName = VERSION_NAME
            )
        }
    }
}


