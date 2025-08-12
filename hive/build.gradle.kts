plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("dagger.hilt.android.plugin")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    kotlin("kapt")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
    id("jacoco")
    id("org.sonarqube") version "4.3.1.3277"
}

android {
    namespace = "com.hive"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

jacoco {
    toolVersion = "0.8.10"
}

val mainClassFiles = fileTree("$buildDir/tmp/kotlin-classes/debug") {
    exclude(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*"
    )
}

tasks.register<JacocoReport>("jacocoFullCoverageReport") {
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(files(mainClassFiles))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    executionData.setFrom(
        fileTree(buildDir) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "outputs/code_coverage/debugAndroidTest/connected/**/*.ec"
            )
        }
    )
}



tasks.named("sonar") {
    dependsOn("jacocoFullCoverageReport")
}

sonarqube {
    properties {
        property("sonar.projectKey", "Hive")
        property("sonar.projectName", "Hive")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/jacocoFullCoverageReport/jacocoFullCoverageReport.xml")
    }
}

dependencies {

    // **Core Libraries**
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.multidex)

    // **Jetpack Compose**
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.junit.ktx)

    // **Jetpack Lifecycle & Navigation**
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation)

    // **Material Design**
    implementation(libs.material3)
    implementation("androidx.compose.material3:material3-window-size-class")

    // **WorkManager & Hilt**
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.navigation.compose)

    // **Icons**
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // **Data Storage**
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)

    // **Networking & Data**
    implementation(libs.easyretrofit)
    testImplementation(libs.mockwebserver)
    implementation(libs.androidx.security.crypto)


    // **Image Loading**
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil.svg)

    // **UI Utilities**
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)
    implementation(libs.androidpdfviewer)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.ksoup)

    // **Serialization**
    implementation(libs.kotlinx.serialization.json)

    // **Third-Party Libraries**
    implementation("com.facebook.stetho:stetho-okhttp3:1.6.0") {
        exclude("okhttp")
    }
    implementation(libs.accompanist.permissions)
    implementation(libs.android.joda)

    // **Dependency Injection (Hilt)**
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // **Platform-Specific**
    implementation(platform(libs.kotlin.bom))

    // **Testing Dependencies**
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockk)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.android)

    // **Robolectric for Unit Testing**
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)

    // **Instrumentation & UI Testing**
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)

    // **Debug Dependencies**
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation("com.google.code.gson:gson:2.13.1")
//    implementation("com.github.saizad:PulseField:4966813efd")
    implementation("com.github.saizad:PulseField:4966813efd0b7e3ffe5133b7f9067f03dcd54997")

}