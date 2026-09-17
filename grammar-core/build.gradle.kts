// Общее ядро клиента: доменные модели, интерфейсы репозиториев, юзкейсы. Без Android —
// переиспользуется будущим iOS-приложением (правила — CLAUDE.md → «Модули проекта»).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.sethan8r"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Типы контракта (SubscriptionTier, ProgressEventType) — часть публичного API домена.
    api(project(":grammar-shared"))
    // Flow в сигнатурах репозиториев и юзкейсов.
    api(libs.kotlinx.coroutines.core)
    // @Inject на юзкейсах: Hilt приложения собирает их без ручной проводки.
    api(libs.javax.inject)

    testImplementation(libs.kotlin.test.junit)
    // Протокол поиска читает настоящую content.db: Room в JVM-тесте недоступен.
    testImplementation(libs.sqlite.jdbc)
}

// Тестовая content.db собирается тем же json_to_db.py из тех же сидов и Room-схемы, что и БД в APK,
// но в build/ ядра: от задач Android-модуля ядро не зависит.
val testContentDb = layout.buildDirectory.file("test-content/content.db")
val contentDbScript = rootProject.file("tasks/tools/json_to_db.py")

val generateTestContentDb by tasks.registering(Exec::class) {
    group = "verification"
    description = "Собирает content.db из JSON-сидов для тестов поиска."

    inputs.file(contentDbScript)
    inputs.dir(rootProject.file("tasks/tools/seed"))
    inputs.dir(rootProject.file("grammar-app/schemas/dev.sethan8r.grammar.app.data.local.content.ContentDatabase"))
    outputs.file(testContentDb)

    // Windows — лаунчер `py`, CI/Linux/macOS — `python3`.
    val python = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "py" else "python3"
    val output = testContentDb.get().asFile
    commandLine(python, contentDbScript.absolutePath, "--out", output.absolutePath)

    doFirst { output.parentFile.mkdirs() }
}

tasks.test {
    dependsOn(generateTestContentDb)
    systemProperty("grammar8r.contentDb", testContentDb.get().asFile.absolutePath)
}

// Ядро переносимо на iOS. Android-классов в classpath нет — такой код просто не скомпилируется;
// а java.*/javax.* на JVM доступны, поэтому их ссылки в коде ловит эта проверка перед компиляцией.
// Разрешён только javax.inject (аннотации для Hilt, см. CLAUDE.md → «Модули проекта»).
val checkCorePortability by tasks.registering {
    group = "verification"
    description = "Запрещает в grammar-core ссылки на платформенные API (java.*, javax.*, android.*)."

    val sources = fileTree("src/main/kotlin") { include("**/*.kt") }
    val moduleDir = projectDir
    inputs.files(sources)

    doLast {
        val platformReference = Regex("""(^|[^\w.])(java|javax|android|androidx)\.[a-z]""")
        val allowed = Regex("""\bjavax\.inject\.""")
        val violations = sources.flatMap { file ->
            file.readLines().mapIndexedNotNull { index, line ->
                val code = line.trimStart()
                val isComment = code.startsWith("//") || code.startsWith("*") || code.startsWith("/*")
                val hit = !isComment && platformReference.containsMatchIn(code) && !allowed.containsMatchIn(code)
                if (hit) "${file.relativeTo(moduleDir)}:${index + 1}: $code" else null
            }
        }
        if (violations.isNotEmpty()) {
            throw GradleException(
                "grammar-core должен оставаться переносимым на iOS — платформенные API запрещены:\n" +
                    violations.joinToString("\n")
            )
        }
    }
}

tasks.compileKotlin {
    dependsOn(checkCorePortability)
}
