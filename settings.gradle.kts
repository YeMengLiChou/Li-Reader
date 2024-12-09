includeBuild("li-utils") {
    dependencySubstitution {
        substitute(module("cn.li.startup:startup-compiler")).using(project(":startup:startup-compiler"))
        substitute(module("cn.li.startup:startup-runtime")).using(project(":startup:startup-runtime"))
    }
}

pluginManagement {
    includeBuild("build-logic")
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// If the project has build-logic module with convention module. (for example: https://github.com/android/nowinandroid/tree/main/build-logic)
// resolved: Unable to make progress running work. There are items queued for execution but none of them can be started
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

println("root: ${gradle.gradleVersion} ${gradle.gradleHomeDir}")

rootProject.name = "LiReader"
include(":app")
include(":baselib")
include(":features")
include(":core:debug")
include(":reader:reader-epub")
