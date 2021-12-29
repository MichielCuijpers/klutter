package dev.buijs.klutter.core.flutter

import dev.buijs.klutter.core.KlutterConfigException
import dev.buijs.klutter.core.KlutterPrinter
import java.io.File
import kotlin.collections.HashMap

/**
 * @author Gillian Buijs
 * @contact https://buijs.dev
 */
internal class AndroidBuildGradlePrinter(
    private val props: HashMap<String, String>,
    private val aarFileLocation: File,
    private val androidLocation: File,
    ): KlutterPrinter {

    override fun print(): String {

        val appId: String = get("app.id").substringAfter("\"").substringBefore("\"")
        val appVersionName: String = get("app.version.name").substringAfter("\"").substringBefore("\"")
        val appVersionCode: Int = get("app.version.code").toInt()
        val compileSdk: Int = get("android.sdk.compile").toInt()
        val targetSdk: Int = get("android.sdk.target").toInt()
        val minSdk: Int = get("android.sdk.minimum").toInt()
        val kotlinVersion: String = get("kotlin.version").substringAfter("\"").substringBefore("\"")
        val flutterSdk: String = get("flutter.sdk.location").substringAfter("\"").substringBefore("\"")
        val kmpAarFile: String = aarFileLocation.relativeTo(androidLocation).toString()

        return """
            |// Autogenerated by Klutter
            |// Do not edit directly
            |// Do not check into VCS
            |// See also: https://buijs.dev/klutter
            |
            |apply plugin: 'com.android.application'
            |apply plugin: 'kotlin-android'
            |apply from: "$flutterSdk/packages/flutter_tools/gradle/flutter.gradle"
            |
            |def keystoreProperties = new Properties()
            |def keystorePropertiesFile = rootProject.file('key.properties')
            |if (keystorePropertiesFile.exists()) {
            |    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
            |}
            |
            |if (!file("$kmpAarFile").exists()) {
            |    throw new GradleException("File '$kmpAarFile' does not exist. Please build kmp module first.")
            |}
            |
            |android {
            |    compileSdkVersion $compileSdk
            |
            |    compileOptions {
            |        sourceCompatibility JavaVersion.VERSION_1_8
            |        targetCompatibility JavaVersion.VERSION_1_8
            |    }
            |
            |    kotlinOptions {
            |        jvmTarget = '1.8'
            |    }
            |
            |    sourceSets {
            |        main.java.srcDirs += 'src/main/kotlin'
            |    }
            |
            |    defaultConfig {
            |        targetSdkVersion $targetSdk
            |        applicationId "$appId"
            |        minSdkVersion $minSdk
            |        versionCode $appVersionCode
            |        versionName "$appVersionName"
            |    }
            |
            |    signingConfigs {
            |        release {
            |            keyAlias keystoreProperties['keyAlias']
            |            keyPassword keystoreProperties['keyPassword']
            |            storeFile keystoreProperties['storeFile'] ? file(keystoreProperties['storeFile']) : null
            |            storePassword keystoreProperties['storePassword']
            |        }
            |    }
            |
            |    buildTypes {
            |        release {
            |            signingConfig signingConfigs.release
            |            shrinkResources false
            |            minifyEnabled false
            |        }
            |    }
            |}
            |
            |flutter {
            |    source '../..'
            |}
            |
            |dependencies {
            |    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
            |    implementation files('$kmpAarFile')
            |}
            |
        """.trimMargin()
    }

    private fun get(key: String) = props[key]
        ?: throw KlutterConfigException("klutter.properties is missing property: $key")

}