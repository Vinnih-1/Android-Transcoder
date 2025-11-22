plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktlint.android)
}

android {
    namespace = "io.github.vinnih.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.vinnih.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

ktlint {
    android.set(true)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material3)
    implementation(libs.tooling.preview)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(project(":library"))
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.uicompose)

    testImplementation(libs.junit)

    debugImplementation(libs.tooling.ui)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
}
