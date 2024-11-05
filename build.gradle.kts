plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.mp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.spotless)
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.spotless.get().pluginId)
    }
    spotless {
        kotlin {
            // target = "**/*.kt"
            targetExclude("${layout.buildDirectory}/**/*.kt")
            targetExclude("bin/**/*.kt")

            ktlint("0.40.0")
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
    }
}
