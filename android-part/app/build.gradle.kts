plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.serdarbsgn.gyrowheel"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.serdarbsgn.gyrowheel"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.01111"

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
}

dependencies {
    implementation(libs.play.services.ads)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}