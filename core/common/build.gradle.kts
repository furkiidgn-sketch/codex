plugins {
    alias(libs.plugins.android.library)
    id("kotlin-kapt")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.sharkoguess.core.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
