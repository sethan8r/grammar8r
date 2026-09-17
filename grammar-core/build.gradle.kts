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
}
