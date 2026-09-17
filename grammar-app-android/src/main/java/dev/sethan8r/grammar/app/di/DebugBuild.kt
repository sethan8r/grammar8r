package dev.sethan8r.grammar.app.di

import javax.inject.Qualifier

/**
 * Qualifier для флага debug-сборки (`BuildConfig.DEBUG`), пробрасываемого через DI.
 * Нужен, чтобы инжектить именно «это» `Boolean`, а не любой другой. Сам флаг провайдит
 * [AppConfigModule] — единственное место, где код ссылается на `BuildConfig`. Потребители
 * (напр. FakeEntitlementsProvider) получают чистый `Boolean` и остаются тестируемыми без Android.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugBuild