import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("maven-publish")
    alias(libs.plugins.kotlin.mp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "dev.vivvvek.seeker"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
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
            implementation(compose.material)
            implementation(libs.annotation)
        }

        androidUnitTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        androidMain {
            dependsOn(commonMain)
            dependencies {
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
        jvmMain.dependsOn(commonMain)
    }
}

dependencies {
    @OptIn(ExperimentalComposeLibrary::class)
    debugImplementation(compose.uiTest)

    testImplementation(libs.junit)
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

publishing {
    publications {
        create<MavenPublication>("bar") {
            groupId = "com.github.abdallah"
            version = "2.0.0"
            // Place the path of your artifact here
            artifact("$buildDir/libs/seeker-jvm.jar")
            artifact(tasks["sourceJar"])
        }
    }
}

tasks["publishBarPublicationToMavenLocal"].dependsOn("jvmJar")