package io.ejekta.posta.client.gui

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.InputUtil
import net.minecraft.text.*

open class KTextAreaWidget(
    override val width: Int,
    override val height: Int
) : KWidget {

    var content = "hi"

    var lineHeight = 10

    val renderer: TextRenderer
        get() = MinecraftClient.getInstance().textRenderer

    private fun getTextLines(str: String): List<String> {
        return renderer.textHandler.wrapLines(str, width, Style.EMPTY).map {
            it.string
        }
    }

    private fun getNumLines(str: String) = getTextLines(str).size

    var cursorPos: Int = 0
        get() {
            return field.coerceIn(0..content.length)
        }

    val keyReactor = KeyReactor().apply {
        onPressDown = { keyCode, scanCode, modifiers ->
            when (keyCode) {
                InputUtil.GLFW_KEY_LEFT -> cursorPos--
                InputUtil.GLFW_KEY_RIGHT -> cursorPos++
            }
        }
        onType = { char, modifiers ->
            println("Char: $char, Mod: $modifiers")
            insert(char.toString())
        }
    }

    private val cursorBefore: String
        get() = if (cursorPos == 0) {
            ""
        } else {
            content.substring(0 until cursorPos)
        }

    private val cursorAfter: String
        get() = if (cursorPos >= content.length) {
            ""
        } else {
            content.substring(cursorPos until content.length)
        }

    val asText: Text
        get() = LiteralText(content)

    private fun insert(newStr: String) {
        val potentialContent = withInserted(newStr)

        // Do not do an insertion if this brings it over the line limit
        if (getNumLines(potentialContent) > (height / lineHeight)) {
            return
        }

        content = potentialContent
        cursorPos += newStr.length
    }

    private fun withInserted(insertion: String): String {
        if (content.isEmpty()) {
            return insertion
        }
        return "$cursorBefore$insertion$cursorAfter"
    }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area {
            reactWith(keyReactor)
            rect(0x0)
            dsl {
                getTextLines(content).forEachIndexed { i, cText ->
                    text(0, i * lineHeight, LiteralText(cText))
                }
            }
        }
    }
}