plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

group = "cn.li.startup"

android {

    namespace = "cn.li.startup"

    defaultConfig {
        minSdk = 21
        compileSdk = 34
    }
}


dependencies {
    api(project(":startup:startup-common"))
}