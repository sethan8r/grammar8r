package dev.sethan8r.grammar.core.usecase.search

/**
 * Приводит слово к основе. Русский — алгоритм Портера (та же схема, что в snowball: отсекаются
 * словоизменительные окончания в области RV), английский — только множественное число.
 *
 * Зачем настоящий стеммер вместо усечения до N символов: усечение не различает слова с общим
 * началом. «прошедшее» и «прошлое» дают одинаковые пять букв, из-за чего запрос про время
 * вытаскивал каждую микротему, где упомянуто прошлое. Портер разводит их честно —
 * `прошедш` против `прошл`.
 */
object WordStemmer {

    fun stem(word: String): String = when {
        word in IRREGULAR -> IRREGULAR.getValue(word)
        word.any { it in CYRILLIC } -> stemRussian(word)
        else -> stemEnglish(word)
    }

    // --- русский (Портер) ---

    private fun stemRussian(word: String): String {
        val rvStart = word.indexOfFirst { it in VOWELS }.takeIf { it >= 0 }?.plus(1) ?: return word
        if (rvStart >= word.length) return word

        var stem = step1(word, rvStart)
        stem = stem.trimEndingIn(rvStart, listOf("и"))
        stem = removeDerivational(stem, word)
        return step4(stem, rvStart)
    }

    /** Деепричастие / возвратность / прилагательное с причастием / глагол / существительное. */
    private fun step1(word: String, rvStart: Int): String {
        val gerund = word.trimGroupEndingIn(rvStart, PERFECTIVE_GERUND_AFTER_VOWEL, PERFECTIVE_GERUND)
        if (gerund != word) return gerund

        val base = word.trimEndingIn(rvStart, REFLEXIVE)
        val adjectival = base.trimEndingIn(rvStart, ADJECTIVE)
        if (adjectival != base) {
            return adjectival.trimGroupEndingIn(rvStart, PARTICIPLE_AFTER_VOWEL, PARTICIPLE)
        }
        val verb = base.trimGroupEndingIn(rvStart, VERB_AFTER_VOWEL, VERB)
        if (verb != base) return verb
        return base.trimEndingIn(rvStart, NOUN)
    }

    /** `ость`/`ост` снимается только из R2 — иначе «мост» превратился бы в «м». */
    private fun removeDerivational(stem: String, word: String): String {
        val r2 = regionR2(word)
        val ending = DERIVATIONAL.firstOrNull { stem.endsWith(it) && stem.length - it.length >= r2 }
        return if (ending != null) stem.dropLast(ending.length) else stem
    }

    private fun step4(stem: String, rvStart: Int): String = when {
        stem.endsWith("нн") -> stem.dropLast(1)
        else -> stem
            .trimEndingIn(rvStart, SUPERLATIVE)
            .let { if (it.endsWith("нн")) it.dropLast(1) else it }
            .trimEndingIn(rvStart, listOf("ь"))
    }

    /** Начало R2 — область после второй пары «гласная + согласная». */
    private fun regionR2(word: String): Int {
        val r1 = regionAfterVowelConsonant(word, 0)
        return regionAfterVowelConsonant(word, r1)
    }

    private fun regionAfterVowelConsonant(word: String, from: Int): Int {
        var i = from
        while (i < word.length - 1) {
            if (word[i] in VOWELS && word[i + 1] !in VOWELS) return i + 2
            i++
        }
        return word.length
    }

    /** Снять самое длинное подходящее окончание, не залезая левее RV. */
    private fun String.trimEndingIn(rvStart: Int, endings: List<String>): String {
        val ending = endings
            .filter { endsWith(it) && length - it.length >= rvStart }
            .maxByOrNull { it.length }
        return if (ending != null) dropLast(ending.length) else this
    }

    /**
     * Окончания, часть которых допустима только после `а`/`я` (в snowball это «группа 1»):
     * так «делая» теряет «я», а «стая» остаётся стаей.
     */
    private fun String.trimGroupEndingIn(
        rvStart: Int,
        afterVowel: List<String>,
        plain: List<String>,
    ): String {
        val direct = trimEndingIn(rvStart, plain)
        if (direct != this) return direct
        val ending = afterVowel
            .filter { endsWith(it) && length - it.length > rvStart }
            .filter { this[length - it.length - 1] in GROUP_ONE_VOWELS }
            .maxByOrNull { it.length }
        return if (ending != null) dropLast(ending.length + 1) else this
    }

    // --- английский ---

    /**
     * Только множественное число: `markers` → `marker`, `questions` → `question`. Снимать `-ed`
     * и `-ing` нельзя — в курсе это не словоформы, а предмет изучения (`used to`, `-ed`).
     */
    private fun stemEnglish(word: String): String = when {
        word.length >= 5 && word.endsWith("ies") -> word.dropLast(3) + "y"
        word.length >= 5 && (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("sses")) ->
            word.dropLast(2)
        word.length >= 4 && word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us") ->
            word.dropLast(1)
        else -> word
    }

    /**
     * Существительные на «-мя» наращивают основу («время» → «времена»), и никакое отсечение
     * окончаний их не сводит — ни здесь, ни в эталонном snowball. Для поиска по курсу это важное
     * слово, поэтому формы заданы явно.
     */
    private val IRREGULAR = mapOf(
        "время" to "врем", "времена" to "врем", "времени" to "врем", "временем" to "врем",
        "временам" to "врем", "временах" to "врем", "временами" to "врем", "времён" to "врем",
        "времен" to "врем",
        "имя" to "им", "имена" to "им", "имени" to "им",
    )

    private const val CYRILLIC = "абвгдежзийклмнопрстуфхцчшщъыьэюя"
    private const val VOWELS = "аеиоуыэюя"
    private const val GROUP_ONE_VOWELS = "ая"

    private val PERFECTIVE_GERUND_AFTER_VOWEL = listOf("вшись", "вши", "в")
    private val PERFECTIVE_GERUND = listOf("ившись", "ывшись", "ивши", "ывши", "ив", "ыв")
    private val REFLEXIVE = listOf("ся", "сь")
    private val ADJECTIVE = listOf(
        "ими", "ыми", "его", "ого", "ему", "ому", "ее", "ие", "ые", "ое", "ей", "ий", "ый", "ой",
        "ем", "им", "ым", "ом", "их", "ых", "ую", "юю", "ая", "яя", "ою", "ею",
    )
    private val PARTICIPLE_AFTER_VOWEL = listOf("нн", "вш", "ющ", "ем", "щ")
    private val PARTICIPLE = listOf("ивш", "ывш", "ующ")
    private val VERB_AFTER_VOWEL = listOf(
        "ете", "йте", "нно", "ла", "на", "ли", "ем", "ло", "но", "ет", "ют", "ны", "ть", "ешь",
        "й", "л", "н",
    )
    private val VERB = listOf(
        "ейте", "уйте", "ила", "ыла", "ена", "ите", "или", "ыли", "ило", "ыло", "ено", "ует",
        "уют", "ены", "ить", "ыть", "ишь", "ей", "уй", "ил", "ыл", "им", "ым", "ен", "ят", "ит",
        "ыт", "ую", "ю",
    )
    private val NOUN = listOf(
        "иями", "ями", "ами", "иях", "ией", "иям", "ием", "иях", "ия", "ья", "ев", "ов", "ие",
        "ье", "еи", "ии", "ей", "ой", "ий", "ям", "ем", "ам", "ом", "ах", "ях", "ию", "ью",
        "а", "е", "и", "й", "о", "у", "ы", "ь", "ю", "я",
    )
    private val DERIVATIONAL = listOf("ость", "ост")
    private val SUPERLATIVE = listOf("ейше", "ейш")
}
