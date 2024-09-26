plugins {
    alias(libs.plugins.reader.android.library)
}

android {
    namespace = "cn.li.reader"
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kxml)
}