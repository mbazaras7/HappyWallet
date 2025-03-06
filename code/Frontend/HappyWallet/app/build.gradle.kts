import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.example.happywallet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.happywallet"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled
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

    packaging {
        resources {
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes.addAll(
                listOf(
                    "META-INF/INDEX.LIST",
                    "META-INF/*.kotlin_module",
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE.txt",
                    "META-INF/license.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/notice.txt",
                    "META-INF/ASL2.0",
                    "META-INF/io.netty.versions.properties"
                )
            )
        }
    }
}

configurations.all {
    resolutionStrategy {
        force ("androidx.core:core:1.13.1")
        force ("androidx.core:core-ktx:1.13.1")
        force ("androidx.test:core:1.6.0")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core:1.13.1")
    implementation("androidx.activity:activity:1.9.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.androidx.rules)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.espresso.intents)
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("com.squareup.retrofit2:retrofit-mock:2.11.0")
    testImplementation("org.slf4j:slf4j-api:2.0.16")
    testImplementation("org.slf4j:slf4j-simple:2.0.16")
    testImplementation("net.bytebuddy:byte-buddy:1.12.10")
    androidTestImplementation("androidx.core:core-ktx:1.13.1")
    androidTestImplementation("androidx.core:core:1.13.1")
    androidTestImplementation("androidx.activity:activity:1.9.0")
    androidTestImplementation("androidx.activity:activity-ktx:1.9.0")
    androidTestImplementation("androidx.activity:activity-compose:1.9.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.16")
    androidTestImplementation("com.squareup.retrofit2:retrofit-mock:2.11.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.4")

    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.8")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //App Navigation
    implementation("androidx.navigation:navigation-compose:2.8.7")

    //Data Visualisation
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //Networking (Coms with backend)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.skydoves:landscapist-glide:2.4.7")

    //JSON Handling
    implementation("com.google.code.gson:gson:2.12.1")

    //Jetpack
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    implementation("io.coil-kt.coil3:coil-compose:3.1.0")

    //Notifications
    implementation("androidx.core:core-ktx:1.13.0")

}