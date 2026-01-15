pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.wagyourtail.xyz/releases")
        maven { url = uri("https://jitpack.io") }
    }

    resolutionStrategy {
        eachPlugin {
            // Check if the requested plugin ID matches your target
            if (requested.id.toString() == "xyz.wagyourtail.unimined") {
                // Map it to the JitPack artifact coordinates
                useModule("com.github.hypherionmc.Unimined:xyz.wagyourtail.unimined.gradle.plugin:8e64092954")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

rootProject.name = "Potatoptimize"
