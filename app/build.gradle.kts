import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.firebase)
}

// Create a variable called keystorePropertiesFile, and initialize it to your
// keystore.properties file, in the rootProject folder.
val keystorePropertiesFile = rootProject.file("keystore.properties")

// Initialize a new Properties() object called keystoreProperties.
val keystoreProperties = Properties()

// Load your keystore.properties file into the keystoreProperties object (only if it exists).
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

// Load local.properties for local development configuration
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "fr.gouv.ami"
    compileSdk = 36

    defaultConfig {
        applicationId = "fr.gouv.ami"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "0.2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("local") {
            dimension = "version"
            applicationIdSuffix = ".local"
            versionNameSuffix = "-local"

            resValue("string", "app_name", "AMI Local")
        }
        create("staging") {
            dimension = "version"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            resValue("string", "app_name", "AMI Staging")
        }
        create("prod") {
            dimension = "version"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Assemble pass used to inject flavored secrets from .env.${flavor} file into BuildConfig and Manifest.
androidComponents.onVariants { variant ->
    val flavor = variant.flavorName ?: return@onVariants
    val envFileRef = rootProject.file("config/.env.${flavor}")
    if (!envFileRef.exists()) return@onVariants

    val props = Properties().apply { envFileRef.inputStream().use(::load) }
    props.forEach { k, v ->
        val key = k.toString().replace(Regex("[^A-Za-z_$0-9]"), "")
        val value = v.toString().removeSurrounding("\"")
        variant.buildConfigFields?.put(key, BuildConfigField("String", "\"$value\"", null))
        variant.manifestPlaceholders.put(key, value)
    }

    val baseHost = props.getProperty("BASE_HOST_STRING") ?: error("BASE_HOST_STRING missing in config/.env.$flavor")
    variant.buildConfigFields?.apply {
        val unquotedBaseHost = baseHost.removeSurrounding("\"")
        put("BASE_URL", BuildConfigField("String", "\"https://$unquotedBaseHost/\"", null))
    }
    val suffix = variant.name.replaceFirstChar(Char::uppercase)
    val gen = tasks.register<GenerateNetworkSecurityConfigTask>(
        "generate${suffix}NetworkSecurityConfig") {
        envFile.set(envFileRef)
    }

    variant.sources.res?.addGeneratedSourceDirectory(gen, GenerateNetworkSecurityConfigTask::outputDir)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.webkit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //firebase
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)

    //crypto
    implementation(libs.androidx.security.crypto)

    //coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //storage
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
}
