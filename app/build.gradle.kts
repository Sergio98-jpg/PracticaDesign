plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

android {
    namespace = "com.example.practicadesign"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.practicadesign"
        minSdk = 26
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.compose.testing)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.composables:icons-lucide:1.0.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.foundation)
    implementation(libs.ui.graphics)
    implementation(libs.animation)
    implementation(libs.ui)

    // Librerías de Google Maps para Jetpack Compose
    implementation("com.google.maps.android:maps-compose:4.3.3") // Composable del mapa
    implementation("com.google.android.gms:play-services-maps:18.2.0") // El SDK base
    implementation("com.google.maps.android:maps-utils-ktx:3.4.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.androidx.ui.text)
    implementation("io.ktor:ktor-client-core:2.3.11")
//    implementation("io.ktor:ktor-client-android:2.3.11")
    implementation("io.ktor:ktor-client-websockets:2.3.11")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.11") // Para JSON
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11") // Para JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-client-cio:2.3.11")

    // Motor Android para Ktor
    implementation("io.ktor:ktor-client-android:2.3.11")
    // Plugin de Logging
    implementation("io.ktor:ktor-client-logging:2.3.11")
    // Serialización de Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Room Database para persistencia local
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // DataStore para preferencias
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.activity:activity-compose:1.11.0")
// o la última versión
    implementation("androidx.compose.ui:ui:1.9.4")
// o la última versión
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.4")
// o la última versión
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation(libs.androidx.appcompat)
// o la última versión
// Para cargar imágenes de URL



    testImplementation("junit:junit:4.13.2")

    // ✅ --- CAMBIOS AQUÍ ---
    // Confía en el BOM para las versiones de testing de Compose
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") // <-- Sin versión

    // Comenta o elimina estas líneas que especifican versiones conflictivas
    // androidTestImplementation("androidx.test.ext:junit:1.3.0")
    // androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}