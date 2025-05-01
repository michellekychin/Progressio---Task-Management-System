plugins {
    alias(libs.plugins.kotlin.compose)
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.21"  // Use Kotlin 2.0.21 to match the classpath version
    id("kotlin-kapt") // Add this line
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

    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation ("androidx.databinding:databinding-runtime:8.9.2")

    // View System (XML) Support
    implementation(libs.androidx.constraintlayout)

    implementation ("com.google.android.material:material:1.12.0")
    implementation (libs.androidx.appcompat)

    // Use Kotlin 2.0.21 to match the Kotlin plugin version
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.10")

    // Room
    implementation("androidx.room:room-runtime:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")  // Add this line for Room annotation processing

    implementation("androidx.compose.material3:material3:1.3.2") // For Material 3
    implementation("androidx.appcompat:appcompat:1.7.0") // For AppCompatDelegate
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // For graphs
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7") // Lifecycle extensions
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7") // ViewModel support


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
