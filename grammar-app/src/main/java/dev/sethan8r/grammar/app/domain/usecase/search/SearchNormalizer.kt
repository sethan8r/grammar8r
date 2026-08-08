package dev.sethan8r.grammar.app.domain.usecase.search

import javax.inject.Inject
import kotlin.math.abs

/** Насколько уверенно слово запроса совпало со словом индекса. */
enum class MatchQuality(val factor: Double) {
    /** Основы совпали. */
    EXACT(1.0),

    /** Слово ещё дописывается: `отриц` → `отрицание`. */
    PREFIX(0.8),

    /** Одна опечатка: `прашедшее` → `прошедшее`. */
    TYPO(0.5),
}

/**
 * Приводит запрос и индексируемые строки к сравнимому виду и решает, совпали ли слова.
 * Применяется одинаково к обеим сторонам — иначе «didn't» в названии микротемы и «did not»
 * в запросе никогда не встретятся.
 *
 * Канон — search_feature_brief.md §8.3.2 (с правками §9.5).
 */
class SearchNormalizer @Inject constructor() {

    /** Слова строки после нормализации и раскрытия сокращений. */
    fun words(text: String): List<String> {
        val cleaned = buildString(text.length) {
            for (ch in text.lowercase()) {
                when {
                    ch in APOSTROPHES -> Unit          // склеиваем: didn't -> didnt
                    ch.isLetterOrDigit() -> append(if (ch == 'ё') 'е' else ch)
                    else -> append(' ')
                }
            }
        }
        return cleaned.split(' ')
            .filter { it.isNotEmpty() }
            .let(::joinClitics)
            .flatMap { CONTRACTIONS[it] ?: listOf(it) }
    }

    /**
     * Основы слов строки — то, что реально сравнивается. Стоп-слова выброшены: «как сказать не
     * делал» ищется по «не делал», иначе «как» вытаскивало бы микротему «Как речь склеивается».
     * Строка из одних стоп-слов сравнивается как есть — лучше так, чем пустой запрос.
     */
    fun stems(text: String): List<String> {
        val stems = words(text).map(WordStemmer::stem)
        return stems.filterNot { it in STOP_STEMS }.ifEmpty { stems }
    }

    /**
     * Предлог, союз или частица: слово-связка, которое само по себе ничего не называет, поэтому
     * в ранжировании стоит дёшево независимо от того, как редко встречается в курсе. Короткие
     * латинские токены (`s`, `ed`, `to`) служебными НЕ считаются: в грамматическом курсе это сам
     * предмет поиска.
     */
    fun isFunctionWord(stem: String): Boolean = stem in FUNCTION_STEMS

    /**
     * Качество совпадения основ или `null`, если не совпали.
     *
     * [allowPrefix] включается для последнего слова запроса: пока пользователь печатает, `маркер`
     * обязан находить `маркеры`. Для остальных слов префикс не разрешён — иначе короткое начало
     * цепляло бы половину курса.
     */
    fun match(queryStem: String, indexStem: String, allowPrefix: Boolean): MatchQuality? = when {
        queryStem == indexStem -> MatchQuality.EXACT

        allowPrefix &&
            queryStem.length >= MIN_PREFIX_LENGTH &&
            indexStem.startsWith(queryStem) -> MatchQuality.PREFIX

        queryStem.length >= MIN_TYPO_LENGTH &&
            indexStem.length >= MIN_TYPO_LENGTH &&
            isOneEditApart(queryStem, indexStem) -> MatchQuality.TYPO

        else -> null
    }

    /** Расстояние Левенштейна ≤ 1 без построения матрицы: одна вставка, удаление или замена. */
    private fun isOneEditApart(a: String, b: String): Boolean {
        if (abs(a.length - b.length) > 1) return false
        var i = 0
        var j = 0
        var edits = 0
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) {
                i++
                j++
                continue
            }
            if (++edits > 1) return false
            when {
                a.length > b.length -> i++
                a.length < b.length -> j++
                else -> { i++; j++ }
            }
        }
        return edits + (a.length - i) + (b.length - j) <= 1
    }

    /**
     * Пробел на месте апострофа: «didn t» -> «didnt». Обрывок из одной-двух букв самостоятельным
     * словом быть не может, поэтому прилипает к предыдущему.
     */
    private fun joinClitics(words: List<String>): List<String> {
        if (words.none { it in CLITICS }) return words
        val result = mutableListOf<String>()
        for (word in words) {
            val previous = result.lastOrNull()
            if (word in CLITICS && previous != null) {
                result[result.lastIndex] = previous + word
            } else {
                result += word
            }
        }
        return result
    }

    private companion object {
        /**
         * Слишком короткое начало цепляет чужие слова: «час» из запроса «часы» дотягивался бы до
         * «част(отность)». Четыре буквы — граница, на которой дописывание ещё работает
         * («отриц» → «отрицание»), а случайные попадания уже отсеиваются.
         */
        const val MIN_PREFIX_LENGTH = 4
        const val MIN_TYPO_LENGTH = 5
        const val APOSTROPHES = "'’`´ʼ"

        /** Хвосты английских сокращений — самостоятельными словами не бывают. */
        val CLITICS = setOf("t", "s", "re", "ve", "ll", "m", "d")

        /**
         * Основы слов, которые ничего не различают: они есть в половине формулировок и только
         * шумят. Английские служебные слова сюда не идут — `to` в `used to` и `have to` значимо.
         *
         * ⚠️ Здесь и ниже перечислены ОСНОВЫ, а не слова: сравнение идёт уже после стемминга
         * (`сказать` → `сказ`). Инвариант проверяет `SearchNormalizerTest`.
         */
        val STOP_STEMS = setOf("как", "что", "эт", "так", "сказ")

        /** Русские предлоги, союзы и частицы — сами по себе ничего не называют. */
        val FUNCTION_STEMS = setOf(
            "в", "во", "о", "об", "на", "к", "ко", "с", "со", "у", "за", "из", "по",
            "до", "от", "для", "при", "над", "под", "про", "без", "через", "и", "а", "но",
            "ил", "же", "ли", "бы", "чтоб", "то", "ж", "б",
        )

        /**
         * Сокращение -> развёрнутая форма. Раскрываются обе стороны сравнения, поэтому
         * «didn't», «didnt», «didn t» и «did not» сходятся в одно и то же.
         */
        val CONTRACTIONS = mapOf(
            "dont" to listOf("do", "not"),
            "doesnt" to listOf("does", "not"),
            "didnt" to listOf("did", "not"),
            "isnt" to listOf("is", "not"),
            "arent" to listOf("are", "not"),
            "wasnt" to listOf("was", "not"),
            "werent" to listOf("were", "not"),
            "havent" to listOf("have", "not"),
            "hasnt" to listOf("has", "not"),
            "hadnt" to listOf("had", "not"),
            "cant" to listOf("can", "not"),
            "cannot" to listOf("can", "not"),
            "couldnt" to listOf("could", "not"),
            "wouldnt" to listOf("would", "not"),
            "shouldnt" to listOf("should", "not"),
            "mustnt" to listOf("must", "not"),
            "wont" to listOf("will", "not"),
        )
    }
}
