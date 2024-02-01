plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.cm20314"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cm20314"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        android.buildFeatures.buildConfig = true
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
    }
    sourceSets {
        getByName("main") {
            java {
                srcDirs("src\\main\\java", "src\\main\\java\\com.example.cm20314\\services", "src\\main\\java", "src\\main\\java\\models", "src\\main\\java", "src\\main\\java\\interfaces")
            }
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate-parent:2.16.1")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.microsoft.appcenter:appcenter-analytics:4.4.5")
    implementation("com.microsoft.appcenter:appcenter-crashes:4.4.5")

}

if (project.hasProperty("APPCENTER_KEY")) {
    android.defaultConfig.buildConfigField("String", "APPCENTER_KEY", "\"${project.findProperty("APPCENTER_KEY")}\"")
}
if (project.hasProperty("API_KEY")) {
    android.defaultConfig.buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY")}\"")
}