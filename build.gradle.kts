import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.0"
	kotlin("plugin.serialization") version "1.6.0"
	id("fabric-loom") version "0.10-SNAPSHOT"
}

// https://modmuss50.me/fabric.html
object Versions {
	object Mod {
		const val ID = "example"
		const val Version = "0.0.1"
		const val Group = "com.example"
	}
	object Fabric {
		const val API = "0.42.9+1.18"
		const val Loader = "0.12.5"
		const val Yarn = "1.18-pre5+build.11"
		const val KotlinAdapter = "1.6.5+kotlin.1.5.31"
	}
	object Dependencies {
		const val Minecraft = "1.18-pre5"
		const val Kotlin = "1.6.0"
		const val KotlinxXSerialization = "1.3.1"
		const val Kambrik = "3.+"
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	withSourcesJar()
	withJavadocJar()
}

project.group = Versions.Mod.Group
version = Versions.Mod.Version

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {
	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${Versions.Dependencies.Minecraft}")
	mappings("net.fabricmc:yarn:${Versions.Fabric.Yarn}:v2")
	modImplementation("net.fabricmc:fabric-loader:${Versions.Fabric.Loader}")

	// Kambrik API
	modImplementation("io.ejekta:kambrik:${Versions.Dependencies.Kambrik}")

	implementation("com.google.code.findbugs:jsr305:3.0.2")

	// Fabric Language Kotlin
	modImplementation(group = "net.fabricmc", name = "fabric-language-kotlin", version = Versions.Fabric.KotlinAdapter)

	// Fabric API
	modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.Fabric.API}")
}


tasks.getByName<ProcessResources>("processResources") {
	filesMatching("fabric.mod.json") {
		expand(
			mutableMapOf<String, String>(
				"modid" to Versions.Mod.ID,
				"version" to Versions.Mod.Version,
				"kotlinVersion" to Versions.Dependencies.Kotlin,
				"fabricApiVersion" to Versions.Fabric.API
			)
		)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}
