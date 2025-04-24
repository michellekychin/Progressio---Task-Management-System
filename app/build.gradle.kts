plugins {
    alias(libs.plugins.kotlin.compose)
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.21"  // Use Kotlin 2.0.21 to match the classpath version
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"  // Use a compatible KSP version with Kotlin 2.0.21
}

android {
    namespace = "com.example.progressiomobileapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.progressiomobileapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("com.google.android.material:material:1.12.0")
    implementation (libs.androidx.appcompat)

    // Use Kotlin 2.0.21 to match the Kotlin plugin version
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.10")

    // KSP version compatible with Kotlin 2.0.21
    ksp("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.21")  // Updated KSP version for Kotlin 2.0.21

    // Room
    implementation("androidx.room:room-runtime:2.7.1")
    ksp("androidx.room:room-compiler-processing:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
