import java.time.Instant

plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("eclipse")
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
    alias(libs.plugins.unimined)
}

base {
    archivesName = modName
}

java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = JavaVersion.toVersion(javaVersion)

spotless {
    format("misc") {
        target("*.gradle.kts", ".gitattributes", ".gitignore")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
    java {
        target("src/**/*.java", "src/**/*.java.peb")
        toggleOffOn()
        importOrder()
        removeUnusedImports()
        cleanthat()
        formatAnnotations()
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        licenseHeader("""/**
 * Copyright (c) 2026 $author
 * This project is Licensed under <a href="$sourceUrl/blob/main/LICENSE">$license</a>
 */""")
    }
}

val fabric: SourceSet by sourceSets.creating
val forge: SourceSet by sourceSets.creating
val neoforge: SourceSet by sourceSets.creating
val sponge: SourceSet by sourceSets.creating

val mainCompileOnly: Configuration by configurations.creating
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val mainAnnotationProcessor: Configuration by configurations.creating
configurations.annotationProcessor.get().extendsFrom(mainAnnotationProcessor)
val fabricCompileOnly: Configuration by configurations.getting
val forgeCompileOnly: Configuration by configurations.getting
val neoforgeCompileOnly: Configuration by configurations.getting
val spongeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    unimined.fabricMaven()
    unimined.minecraftForgeMaven()
    unimined.neoForgedMaven()
    unimined.spongeMaven()
    maven("https://maven.neuralnexus.dev/releases")
    maven("https://maven.neuralnexus.dev/snapshots")
}

unimined.minecraft {
    version(minecraftVersion)
    defaultRemapJar = false
}

unimined.minecraft(fabric) {
    combineWith(sourceSets.main.get())
    version(minecraftVersion)
    defaultRemapJar = false
}

unimined.minecraft(forge) {
    combineWith(sourceSets.main.get())
    version(minecraftVersion)
    defaultRemapJar = false
}

unimined.minecraft(neoforge) {
    combineWith(sourceSets.main.get())
    version(minecraftVersion)
    defaultRemapJar = false
}

unimined.minecraft(sponge) {
    combineWith(sourceSets.main.get())
    version(minecraftVersion)
    defaultRemapJar = false
}

dependencies {
    mainCompileOnly(libs.asm)
    mainCompileOnly(libs.annotations)
    mainCompileOnly(libs.mixin)
    mainCompileOnly(libs.mixinextras)
    implementation("dev.neuralnexus.taterlib.lite:base:0.2.0-SNAPSHOT")
    implementation("dev.neuralnexus.taterlib.lite:metadata:0.2.0-SNAPSHOT")
    implementation("org.tomlj:tomlj:1.1.1")
    implementation("com.moulberry:mixinconstraints:1.0.8")
}

tasks.withType<ProcessResources> {
    filesMatching(listOf(
        "fabric.mod.json",
        "pack.mcmeta",
        "META-INF/mods.toml",
        "META-INF/neoforge.mods.toml",
        "META-INF/sponge_plugins.json",
        "assets/potatoptimize/potatoptimize-mixin-config-default.properties",
    )) {
        expand(project.properties)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")

    from(
        fabric.output,
        forge.output,
        neoforge.output,
        sponge.output,
    )

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modName,
                "Specification-Version" to version,
                "Specification-Vendor" to "SomeVendor",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "SomeVendor",
                "Implementation-Timestamp" to Instant.now().toString(),
                "FMLCorePluginContainsFMLMod" to "true",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "MixinConfigs" to "$modId.mixins.vanilla.json"
            )
        )
    }

    from(listOf("README.md", "LICENSE")) {
        into("META-INF")
    }
}

tasks.jar {
    enabled = false
}
