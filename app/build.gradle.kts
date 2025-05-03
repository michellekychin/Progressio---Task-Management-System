plugins {
    alias(libs.plugins.kotlin.compose)
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.21"  // Ensure consistent Kotlin versions
    id("kotlin-kapt") // Add this line for annotation processing
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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    // JavaMail API
    implementation ("com.sun.mail:android-mail:1.6.0")
    implementation ("com.sun.mail:android-activation:1.6.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")  // OkHttp dependency
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")  // Optional: for logging


    // Lifecycle Coroutine Support
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7") // Ensure lifecycle-ktx is there

    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation ("androidx.databinding:databinding-runtime:8.9.2")

    // View System (XML) Support
    implementation(libs.androidx.constraintlayout)


    // Room Coroutines support
    implementation("androidx.room:room-ktx:2.7.1")

    // Coroutines for background tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")


    implementation(libs.androidx.appcompat) // Add this line


    // View System (XML) Support
    implementation(libs.androidx.constraintlayout)

    // Material Components
    implementation("com.google.android.material:material:1.6.1") // Updated to latest stable version

    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")

    // Room for local database
    implementation("androidx.room:room-runtime:2.7.1")
    implementation(libs.androidx.recyclerview)
    kapt("androidx.room:room-compiler:2.7.1")

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
