package dev.sethan8r.grammar.app.ui.components.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import dev.sethan8r.grammar.app.ui.theme.TextSecondary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Вердикт `✓`/`✗`: гасит сломанную вставку перед собой, иначе остаётся иконкой. */
class InlineMarkdownTest {

    private fun parse(raw: String) = parseInlineMarkdown(
        raw = raw,
        correctColor = Color(0xFF5BC98A),
        incorrectColor = Color(0xFFF46E72),
        arrowColor = Color.White,
    )

    /** Погашен ли фрагмент [part]: второстепенный цвет и обычный вес вместо жирного. */
    private fun isDimmed(raw: String, part: String): Boolean {
        val parsed = parse(raw)
        val at = parsed.text.text.indexOf(part)
        assertTrue(at >= 0, "нет фрагмента «$part» в «${parsed.text.text}»")
        val style = parsed.text.spanStyles
            .filter { at >= it.start && at < it.end }
            .fold(null as androidx.compose.ui.text.SpanStyle?) { acc, r -> acc?.merge(r.item) ?: r.item }
        return style?.color == TextSecondary &&
            style.fontWeight == FontWeight.Normal &&
            style.textDecoration == TextDecoration.LineThrough
    }

    @Test
    fun `сломанное гаснет без крестика, у верного галочка вплотную`() {
        val raw = "**I a student** ✗ вместо **I am a student** ✓."

        // Крестика в тексте нет, галочка осталась и прижата к слову — пробел перед ней съеден.
        assertEquals("I a student вместо I am a student✓.", parse(raw).text.text)
        assertTrue(isDimmed(raw, "I a student"))
        assertTrue(!isDimmed(raw, "I am a student"))
    }

    @Test
    fun `бэктик-вставка тоже принимает вердикт`() {
        assertTrue(isDimmed("По-русски `Am a student` ✗ — без подлежащего.", "Am a student"))
    }

    @Test
    fun `значок после голого текста и в одиночку остаётся иконкой`() {
        // Вердикт относится ко всему примеру, а жирным выделен только предлог — вставку не трогаем.
        val whole = parse("The class starts **at** 9 am. ✓")
        assertTrue(whole.text.text.endsWith("9 am. ✓"), whole.text.text)

        // Ячейка таблицы TrueFalse: гасить нечего.
        assertEquals("✗", parse("✗").text.text)
    }

    @Test
    fun `курсив и транскрипция между вставкой и значком снимают эффект`() {
        assertTrue(!isDimmed("**I a student** *(я студент)* ✗", "I a student"))
        assertTrue(!isDimmed("**worked** [[wɜːkt]] ✗", "worked"))
    }
}
