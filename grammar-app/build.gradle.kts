plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "dev.sethan8r.grammar.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "dev.sethan8r.grammar.app"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"

}

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Room: экспорт схем в VCS (identity hash) — json_to_db.py (Шаг C) генерирует content.db из них.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Шаг C: сборка content.db из JSON-сидов перед упаковкой assets.
// json_to_db.py берёт структуру из экспортированной Room-схемы (identity hash обязан совпасть),
// данные — из tasks/tools/seed/**/*.json. Результат — build-артефакт, в VCS не коммитим (см. .gitignore).
// Папка экспортированных Room-схем content.db. Конкретную версию (последний N.json) выбирает
// json_to_db.py в рантайме — так bump версии БД не требует правки этого пути.
val contentDbSchemaDir = file(
    "schemas/dev.sethan8r.grammar.app.data.local.content.ContentDatabase"
)
val contentDbOutput = file("src/main/assets/content.db")
val seedDir = rootProject.file("tasks/tools/seed")
val seedScript = rootProject.file("tasks/tools/json_to_db.py")

val generateContentDb by tasks.registering(Exec::class) {
    group = "build"
    description = "Собирает content.db из JSON-сидов по Room-схеме (Шаг C)."

    inputs.file(seedScript)
    inputs.dir(contentDbSchemaDir)
    inputs.dir(seedDir)
    outputs.file(contentDbOutput)

    // Windows — лаунчер `py`, CI/Linux/macOS — `python3`.
    val python = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "py" else "python3"
    // Без --schema: json_to_db.py сам берёт последнюю версию схемы из папки ContentDatabase
    // (в рантайме, после ksp — так свежий N.json уже на месте).
    commandLine(
        python, seedScript.absolutePath,
        "--out", contentDbOutput.absolutePath,
    )

    doFirst { contentDbOutput.parentFile.mkdirs() }

    // content.db строится ИЗ экспортированной Room-схемы (её пишет ksp<Variant>Kotlin),
    // поэтому генерируем БД ПОСЛЕ KSP. Живая коллекция упорядочивает только активный вариант
    // (в debug-сборке release-ksp не запускается, mustRunAfter для него инертен).
    mustRunAfter(tasks.matching { it.name.startsWith("ksp") && it.name.endsWith("Kotlin") })
}

// ...и ДО упаковки ассетов: merge<Variant>Assets ждёт генерацию БД.
tasks.matching { it.name.startsWith("merge") && it.name.endsWith("Assets") }.configureEach {
    dependsOn(generateContentDb)
}

// Тесты поиска читают собранную content.db, поэтому `gradlew test` сначала пересобирает её
// из сидов — иначе протокол гонялся бы по устаревшему контенту.
tasks.withType<Test>().configureEach {
    dependsOn(generateContentDb)
    systemProperty("grammar8r.contentDb", contentDbOutput.absolutePath)
}

dependencies {
    implementation(project(":grammar-core"))
    implementation(project(":grammar-shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Hilt (DI)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room (две БД: content.db / user.db — наполняется в шаге B)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    // Тесты поиска гоняются по настоящей content.db (см. task testDependsOnContentDb ниже):
    // JVM-тесту недоступен Room, поэтому БД открывается sqlite-драйвером напрямую.
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.sqlite.jdbc)

    // kotlinx.serialization (type-safe навигация + парсинг JSON-блоков теории)
    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.androidx.compose.ui.tooling)
}