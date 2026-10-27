import java.util.Properties

plugins {
    id("com.android.application")
}

// Upload key: read from environment (CI) or keystore.properties (local), never committed.
val localKeys = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun secret(env: String, prop: String): String? = System.getenv(env) ?: localKeys.getProperty(prop)
val ksFile = secret("KEYSTORE_FILE", "storeFile")

android {
    namespace = "io.github.kayaworks.zikirmatik"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.kayaworks.zikirmatik"
        minSdk = 26
        targetSdk = 36
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
        versionName = "1.0"
    }

    signingConfigs {
        create("upload") {
            if (ksFile != null) {
                storeFile = file(ksFile)
                storePassword = secret("KEYSTORE_PASSWORD", "storePassword")
                keyAlias = secret("KEY_ALIAS", "keyAlias")
                keyPassword = secret("KEY_PASSWORD", "keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            if (ksFile != null) signingConfig = signingConfigs.getByName("upload")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
