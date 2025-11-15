plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint.android)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.github.vinnih.androidtranscoder"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = false
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

ktlint {
    android.set(true)
    verbose.set(true)
}

dependencies {
    implementation(libs.tandroid.lame) {
        exclude(group = "com.android.support")
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.media3.extractor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
