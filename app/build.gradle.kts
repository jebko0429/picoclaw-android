plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.picoclaw.app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.picoclaw.app"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"

        // Needed when loading native libs from gomobile bind.
        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        // Foreground service + notification permission.
        resourceConfigurations += listOf("en")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources.excludes += setOf(
            "META-INF/AL2.0",
            "META-INF/LGPL2.1"
        )
    }

    // Keep if you use local http from WebView.
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.webkit:webkit:1.6.1")

    // Add the gomobile AAR once built: place in app/libs and uncomment below.
    // implementation(files("libs/picoclaw.aar"))
}
