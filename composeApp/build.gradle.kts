import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation("com.github.kwhat:jnativehook:2.2.2")
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            includeAllModules = true
            targetFormats(TargetFormat.Dmg)
            packageName = "KMR"
            packageVersion = "1.0.0"
            jvmArgs("-Dapple.awt.application.appearance=system")

            macOS {
                bundleID = "at.arkulpa.kmr"
                signing {
                    sign.set(true)
                    identity.set("Developer ID Application: arkulpa GmbH (6VC52FR9XZ)")
                    keychain.set("/Users/linusweiss/Library/Keychains/login.keychain-db")
                }
                notarization {
                    appleID.set("info@arkulpa.at")
                    password.set("vbhn-hbtv-bxjt-umdk")
                    teamID.set("6VC52FR9XZ")
                }
            }
        }
    }
}
