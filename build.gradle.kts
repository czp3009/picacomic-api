import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.maven.publish)
}

group = "com.hiczp"
version = "0.1.0"
description = "A Kotlin Multiplatform client for the PicACG v2 API"

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()
    jvmToolchain(17)

    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    android {
        namespace = "com.hiczp.picacomic.api"
        compileSdk = 36
        minSdk = 21
        withHostTest {}
    }

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }

    mingwX64()
    linuxX64()
    linuxArm64()
    macosArm64()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.client.core)
            api(libs.ktorfit.lib.light)
            api(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

tasks.withType<AbstractTestTask>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

// The test is platform-neutral, but running it once on the JVM keeps the build deterministic.
tasks.matching {
    it.name.endsWith("Test") &&
            it.name != "jvmTest" &&
            !it.name.startsWith("clean") &&
            !it.name.startsWith("compile") &&
            !it.name.startsWith("link")
}.configureEach {
    enabled = false
}

val kspCommonMainKotlinMetadata = tasks.matching {
    it.name == "kspCommonMainKotlinMetadata"
}

tasks.matching {
    it.name == "sourcesJar" || it.name.endsWith("SourcesJar")
}.configureEach {
    dependsOn(kspCommonMainKotlinMetadata)
}

mavenPublishing {
    coordinates(project.group.toString(), project.name, project.version.toString())
    publishToMavenCentral()
    signAllPublications()
    configure(KotlinMultiplatform(javadocJar = JavadocJar.Empty()))

    pom {
        name = "PicaComic API"
        description = project.description
        inceptionYear = "2019"
        url = "https://github.com/czp3009/picacomic-api"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "czp3009"
                name = "czp3009"
                email = "czp3009@gmail.com"
                url = "https://github.com/czp3009"
            }
        }

        scm {
            url = "https://github.com/czp3009/picacomic-api"
            connection = "scm:git:git://github.com/czp3009/picacomic-api.git"
            developerConnection = "scm:git:ssh://git@github.com/czp3009/picacomic-api.git"
        }
    }
}
