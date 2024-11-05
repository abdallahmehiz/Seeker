import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.mp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
}


// https://github.com/JetBrains/compose-multiplatform/issues/3123#issuecomment-1647435023
val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val targetArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val version = libs.versions.skiko.get() // libs.versions.skiko // or any more recent version
val target = "${targetOs}-${targetArch}"

android {
    namespace = "dev.vivvvek.seekerdemo"
    compileSdk = 35

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "dev.vivvvek.seekerdemo"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    androidTarget()

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "app"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting
        commonMain.dependencies {
            implementation(project(":seeker"))

            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation(libs.navigation.compose)
            implementation(libs.lifecycle.viewmodel)
        }
        androidMain {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.activity.compose)
                implementation(compose.material)
                implementation(compose.preview)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        iosMain.dependsOn(commonMain)

        val jvmMain by getting
        jvmMain.dependencies {
            implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$version")
            implementation(libs.collection)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(compose.runtime)
            implementation(compose.desktop.currentOs)
        }
        jvmMain.dependsOn(commonMain)
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "dev.vivvvek.seekerdemo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.vivvvek.seekerdemo"
            packageVersion = "1.0.0"
        }
    }
}
