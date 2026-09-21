plugins {
    java
    alias(libs.plugins.spring.boot)
}

group = "dev.sethan8r"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)

    // DTO контракта API. Сериализуются ТОЛЬКО через kotlinx: Jackson игнорирует @SerialName
    // и тихо разводит контракт с клиентом — см. phase4_server.md → «Шероховатости».
    implementation(project(":grammar-shared"))

    testImplementation(libs.spring.boot.starter.test)
}

tasks.test {
    useJUnitPlatform()
}