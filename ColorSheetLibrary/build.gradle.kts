plugins {
    alias(libs.plugins.android.library)
//    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    `maven-publish`
}

android {
    namespace = "aok.nori.colorsheetlibrary"
    compileSdk = 34

    defaultConfig {
//        applicationId = "aok.nori.colorsheetlibrary"
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.aokdev"
            artifactId = "color-sheet-library"
            version = "1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}