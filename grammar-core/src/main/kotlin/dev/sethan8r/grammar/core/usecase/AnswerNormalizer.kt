package dev.sethan8r.grammar.core.usecase

/**
 * Нормализация введённого ответа для упражнений с вводом текста (TEXT_INPUT и далее). Приводит
 * строку к канону, чтобы «don't» и «do not», «I'll» и «I will» и т.п. считались одним ответом —
 * иначе верный ответ в другой форме записи валится как ошибка.
 *
 * Канон строится так: нижний регистр → единый апостроф → раскрытие сокращений в полную форму →
 * удаление любых знаков препинания/символов → схлопывание пробелов. Нормализуем ОБЕ стороны сравнения
 * (и ввод, и принимаемые ответы) — поэтому совпадение работает в любую сторону (автор написал полную
 * форму, юзер ввёл сокращение, и наоборот). Пунктуация и лишние пробелы игнорируются: случайная
 * запятая/точка/двойной пробел в ответе не валит верный по смыслу ввод (фидбэк 2026-06-21).
 *
 * Чистый Kotlin, без Android — покрывается юнит-тестами.
 */
object AnswerNormalizer {

    fun normalize(raw: String): String {
        var s = raw.lowercase().unifyApostrophes()
        // Раскрытие сокращений — ДО удаления пунктуации (иначе «isn't» не совпадёт с ключом карты).
        for ((regex, replacement) in expansions) {
            s = regex.replace(s, replacement)
        }
        // Любые знаки препинания и символы (запятые, точки, апострофы, тире, кавычки …) не значимы.
        s = PUNCTUATION.replace(s, "")
        return s.replace(WHITESPACE, " ").trim()
    }

    /** Совпадают ли эталон и ввод после нормализации (регистр, сокращения, пунктуация, пробелы). */
    fun matches(expected: String, input: String): Boolean = normalize(expected) == normalize(input)

    private fun String.unifyApostrophes(): String =
        replace('’', '\'') // ’ правый одинарный
            .replace('‘', '\'') // ‘ левый одинарный
            .replace('ʼ', '\'') // ʼ модификатор
            .replace('´', '\'') // ´ акут
            .replace('`', '\'')

    private val WHITESPACE = Regex("\\s+")

    /** Любые знаки препинания (`\p{P}`) и символы (`\p{S}`) — Unicode, не только ASCII. */
    private val PUNCTUATION = Regex("[\\p{P}\\p{S}]")

    /**
     * Сокращение → полная форма. Ключи — в нижнем регистре с обычным апострофом (ввод к этому уже
     * приведён). Неоднозначные раскрываются в самый частый вариант (`'s`→is, `'d`→would); если в
     * упражнении нужна другая трактовка — автор добавляет её в `alternatives`.
     */
    private val CONTRACTIONS: Map<String, String> = mapOf(
        // --- отрицания (n't) ---
        "aren't" to "are not",
        "isn't" to "is not",
        "wasn't" to "was not",
        "weren't" to "were not",
        "haven't" to "have not",
        "hasn't" to "has not",
        "hadn't" to "had not",
        "won't" to "will not",
        "wouldn't" to "would not",
        "don't" to "do not",
        "doesn't" to "does not",
        "didn't" to "did not",
        "can't" to "can not",
        "cannot" to "can not",
        "couldn't" to "could not",
        "shouldn't" to "should not",
        "mustn't" to "must not",
        "mightn't" to "might not",
        "needn't" to "need not",
        "shan't" to "shall not",
        "daren't" to "dare not",
        "oughtn't" to "ought not",
        "ain't" to "is not",
        // --- will / shall ('ll) ---
        "i'll" to "i will",
        "you'll" to "you will",
        "he'll" to "he will",
        "she'll" to "she will",
        "it'll" to "it will",
        "we'll" to "we will",
        "they'll" to "they will",
        "that'll" to "that will",
        "this'll" to "this will",
        "who'll" to "who will",
        "what'll" to "what will",
        "there'll" to "there will",
        // --- am / is / are ('m, 's, 're) ---
        "i'm" to "i am",
        "you're" to "you are",
        "we're" to "we are",
        "they're" to "they are",
        "he's" to "he is",
        "she's" to "she is",
        "it's" to "it is",
        "that's" to "that is",
        "this's" to "this is",
        "there's" to "there is",
        "here's" to "here is",
        "who's" to "who is",
        "what's" to "what is",
        "where's" to "where is",
        "when's" to "when is",
        "why's" to "why is",
        "how's" to "how is",
        "let's" to "let us",
        // --- have / has ('ve) ---
        "i've" to "i have",
        "you've" to "you have",
        "we've" to "we have",
        "they've" to "they have",
        "would've" to "would have",
        "could've" to "could have",
        "should've" to "should have",
        "must've" to "must have",
        "might've" to "might have",
        "who've" to "who have",
        "there've" to "there have",
        // --- would / had ('d → would) ---
        "i'd" to "i would",
        "you'd" to "you would",
        "he'd" to "he would",
        "she'd" to "she would",
        "it'd" to "it would",
        "we'd" to "we would",
        "they'd" to "they would",
        "that'd" to "that would",
        "there'd" to "there would",
        "who'd" to "who would",
    )

    /**
     * Прекомпилированные правила раскрытия. Границы — отрицательные lookbehind/lookahead на букву или
     * апостроф, чтобы заменять сокращение целиком, а не его кусок внутри другого слова.
     */
    private val expansions: List<Pair<Regex, String>> = CONTRACTIONS.map { (key, full) ->
        Regex("(?<![\\p{L}'])" + Regex.escape(key) + "(?![\\p{L}'])") to full
    }
}