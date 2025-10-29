import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

id("kotlin-kapt")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sharkoguess.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sharkoguess"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "APPLE_MUSIC_DEV_TOKEN", "\"" + (gradleLocalProperties(rootDir).getProperty("APPLE_MUSIC_DEV_TOKEN") ?: "") + "\"")
            buildConfigField("String", "APPLE_STOREFRONT", "\"" + (gradleLocalProperties(rootDir).getProperty("APPLE_STOREFRONT") ?: "tr") + "\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "APPLE_MUSIC_DEV_TOKEN", "\"" + (gradleLocalProperties(rootDir).getProperty("APPLE_MUSIC_DEV_TOKEN") ?: "") + "\"")
            buildConfigField("String", "APPLE_STOREFRONT", "\"" + (gradleLocalProperties(rootDir).getProperty("APPLE_STOREFRONT") ?: "tr") + "\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:player"))
    implementation(project(":data:applemusic"))
    implementation(project(":data:firebase"))
    implementation(project(":domain"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:artistpicker"))
    implementation(project(":feature:category"))
    implementation(project(":feature:solo"))
    implementation(project(":feature:versus"))
    implementation(project(":feature:leaderboard"))
    implementation(project(":feature:settings"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation(libs.play.services.auth)
}
