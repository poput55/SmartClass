plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartclass"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smartclass"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    
    // Firebase SDKs (добавьте нужные модули)
    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Firestore (база данных)
    implementation("com.google.firebase:firebase-firestore")
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation(libs.androidx.core.splashscreen)

    implementation("br.com.devsrsouza.compose.icons:eva-icons:1.1.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Unit testing
    testImplementation("junit:junit:4.13.2")

    // Android testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}