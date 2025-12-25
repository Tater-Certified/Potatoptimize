import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.time.Instant

plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("eclipse")
    id("xyz.wagyourtail.unimined") version "8e64092954"
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
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
        googleJavaFormat("1.24.0")
            .aosp()
            .formatJavadoc(true)
            .reorderImports(true)
        formatAnnotations()
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        licenseHeader("""/**
 * Copyright (c) 2025 $author
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
val modImplementation: Configuration by configurations.creating
val fabricModImplementation: Configuration by configurations.creating {
    extendsFrom(modImplementation)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<RemapJarTask> {
    mixinRemap {
        enableBaseMixin()
        disableRefmap()
    }
}

repositories {
    mavenCentral()
    unimined.fabricMaven()
    unimined.minecraftForgeMaven()
    unimined.neoForgedMaven()
    unimined.parchmentMaven()
    unimined.spongeMaven()
    maven("https://jitpack.io")
    maven("https://maven.neuralnexus.dev/snapshots")
}

unimined.minecraft {
    version(minecraftVersion)
    mappings {
        parchment(parchmentMinecraft, parchmentVersion)
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
}

unimined.minecraft(fabric) {
    combineWith(sourceSets.main.get())
    fabric {
        loader(fabricLoaderVersion)
    }
    defaultRemapJar = true
}

tasks.register<ShadowJar>("relocateFabricJar") {
    dependsOn("remapFabricJar")
    from(zipTree(tasks.getByName<RemapJarTask>("remapFabricJar").asJar.archiveFile.get().asFile))
    archiveClassifier.set("fabric-relocated")
    relocate("com.github.tatercertified.vanilla", "com.github.tatercertified.y_intmdry")
}

unimined.minecraft(forge) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
    }
    defaultRemapJar = true
}

unimined.minecraft(neoforge) {
    combineWith(sourceSets.main.get())
    neoForge {
        loader(neoForgeVersion)
    }
    defaultRemapJar = true
}

unimined.minecraft(sponge) {
    combineWith(sourceSets.main.get())
    defaultRemapJar = true
}

dependencies {
    mainCompileOnly(libs.asm)
    mainCompileOnly(libs.annotations)
    mainCompileOnly(libs.mixin)
    mainCompileOnly(libs.mixinextras)
    mainCompileOnly("com.github.Tater-Certified:MixinConstraints:4856759a06")
    spongeCompileOnly("org.spongepowered:spongeapi:$spongeVersion")
    implementation("dev.neuralnexus.taterlib.lite:base:0.2.0-SNAPSHOT")
    implementation("dev.neuralnexus.taterlib.lite:metadata:0.2.0-SNAPSHOT")
    implementation("org.tomlj:tomlj:1.1.1")
}

tasks.withType<ProcessResources> {
    filesMatching(listOf(
        "fabric.mod.json",
        "pack.mcmeta",
        "assets/potatoptimize/potatoptimize-mixin-config-default.properties",
        "META-INF/mods.toml",
        "META-INF/neoforge.mods.toml",
        "plugin.yml",
        "ignite.mod.json",
        "META-INF/sponge_plugins.json",
    )) {
        expand(project.properties)
    }
}

tasks.jar {
    dependsOn("relocateFabricJar")

    from(
        zipTree(tasks.getByName<Jar>("relocateFabricJar").archiveFile.get().asFile),
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
tasks.build.get().dependsOn("spotlessApply")
