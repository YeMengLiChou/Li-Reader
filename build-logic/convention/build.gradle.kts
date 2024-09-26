import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "cn.li.reader.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}


val pluginsPackage = "cn.li.reader.convention"
gradlePlugin {
    plugins {
        register("convention.android.application") {
            id = "li.reader.convention.android.application"
            implementationClass = "$pluginsPackage.AndroidApplicationConventionPlugin"
        }
        register("convention.android.library") {
            id = "li.reader.convention.android.library"
            implementationClass = "$pluginsPackage.AndroidLibraryConventionPlugin"
        }
        register("convention.room") {
            id = "li.reader.convention.room"
            implementationClass = "$pluginsPackage.AndroidRoomConventionPlugin"
        }


        register("convention.flavor") {
            id = "li.reader.convention.flavor"
            implementationClass = "$pluginsPackage.flavor.FlavorConventionPlugin"
        }
    }
}