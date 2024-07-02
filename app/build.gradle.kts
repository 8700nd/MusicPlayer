plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.examplemusicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.examplemusicplayer"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}


val coreKtxVersion = "1.6.0"
val appCompatVersion = "1.6.1"
val materialVersion = "1.12.0"
val constraintLayoutVersion = "2.1.4"
val navigationVersion = "2.7.7"
val koinVersion = "3.1.6"
val junitVersion = "4.13.2"
val espressoVersion = "3.3.0"
val recyclerView = "1.3.2"
val lifecycle = "2.7.0"
val timberVersion = "5.0.1"
val lottieVersion = "6.4.1"

dependencies {
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.recyclerview:recyclerview:$recyclerView")
    implementation("com.jakewharton.timber:timber:$timberVersion")

    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.coil-kt:coil:2.6.0")
    implementation("androidx.test:monitor:1.6.1")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("junit:junit:4.12")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")
}
