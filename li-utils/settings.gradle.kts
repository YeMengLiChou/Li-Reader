pluginManagement {
    includeBuild("./../build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("./../gradle/libs.versions.toml"))
        }
    }
}

println("li-utils: ${gradle.gradleVersion} ${gradle.gradleHomeDir}")

rootProject.name = "li-utils"
include(":startup:startup-common")
include(":startup:startup-compiler")
include(":startup:startup-runtime")
