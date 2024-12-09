dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
println("logic: ${gradle.gradleVersion} ${gradle.gradleHomeDir}")
rootProject.name = "build-logic"
include(":convention")