plugins {
    id("com.android.application")
}

android {
    namespace = "com.cm20314.mapapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cm20314.mapapp"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.microsoft.appcenter:appcenter-analytics:5.0.4")
    implementation("com.microsoft.appcenter:appcenter-crashes:5.0.4")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
}

if (project.hasProperty("APPCENTER_KEY")) {
    android.defaultConfig.buildConfigField(
        "String",
        "APPCENTER_KEY",
        "\"${project.findProperty("APPCENTER_KEY")}\""
    )
}
if (project.hasProperty("API_KEY")) {
    android.defaultConfig.buildConfigField(
        "String",
        "API_KEY",
        "\"${project.findProperty("API_KEY")}\""
    )
}