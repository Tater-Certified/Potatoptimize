pluginManagement {
    repositories {
        // maven("https://maven.neuralnexus.dev/mirror")
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.wagyourtail.xyz/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "Potatoptimize"