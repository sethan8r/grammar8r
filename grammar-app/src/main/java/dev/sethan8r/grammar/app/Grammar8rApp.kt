package dev.sethan8r.grammar.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Точка входа Hilt. Граф зависимостей строится здесь один раз на процесс.
 */
@HiltAndroidApp
class Grammar8rApp : Application()