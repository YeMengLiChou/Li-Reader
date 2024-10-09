plugins {
    alias(libs.plugins.reader.android.library)
}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.mmkv)

    implementation(libs.kotlinx.coroutines.guava)
}