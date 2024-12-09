plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

group = "cn.li.startup"



java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
}