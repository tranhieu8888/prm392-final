plugins {
    id("com.android.application")
//    id("com.google.gms.google-services") // nếu dùng Firebase
}

android {
    namespace = "com.teamapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.teamapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { buildConfig = true }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.activity:activity:1.9.2")
    implementation("androidx.fragment:fragment:1.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.work:work-runtime:2.11.0")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // WorkManager
    implementation("androidx.work:work-runtime:2.9.1")

    // Firebase Cloud Messaging
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-messaging")

    // SignalR
    implementation("com.microsoft.signalr:signalr:6.0.9")

    // EncryptedSharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
