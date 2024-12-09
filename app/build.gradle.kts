plugins {
    alias(libs.plugins.reader.android.application)
    alias(libs.plugins.reader.flavor)
//    id("therouter")
}

android {
    namespace = "cn.li.app"
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":baselib"))

//    ksp(libs.startup.compiler)
    implementation(libs.startup.runtime)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}