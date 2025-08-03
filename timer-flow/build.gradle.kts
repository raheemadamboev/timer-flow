import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    id("maven-publish")
}

android {
    namespace = "xyz.teamgravity.timer_flow"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }

    lint {
        targetSdk = libs.versions.sdk.target.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        target {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_17
            }
        }
    }

    packaging {
        resources {
            pickFirsts.add("META-INF/atomicfu.kotlin_module")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {

    // coroutines
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.raheemadamboev"
            artifactId = "timer-flow"
            version = "1.0.5"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}