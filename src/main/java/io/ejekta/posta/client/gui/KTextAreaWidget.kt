package io.ejekta.posta.client.gui

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.text.textLiteral
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class KTextAreaWidget(
    override val width: Int,
    override val height: Int,
    val maxSize: Int,
    val sizeType: LineBreakType
) : KWidget {

    var content = "hi"

    val renderer: TextRenderer
        get() = MinecraftClient.getInstance().textRenderer

    enum class LineBreakType {
        WIDTH,
        LINES
    }

    fun getTextLines(str: String): MutableList<OrderedText> {
        val wrapped = renderer.wrapLines(LiteralText(str), 50)
        return wrapped
    }

    var cursorPos: Int = 0
        get() {
            return field.coerceIn(0..content.length)
        }

    val reactor = KeyReactor().apply {
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

    val cursorBefore: String
        get() = if (cursorPos == 0) {
            ""
        } else {
            content.substring(0 until cursorPos)
        }

    val cursorAfter: String
        get() = if (cursorPos >= content.length) {
            ""
        } else {
            content.substring(cursorPos until content.length)
        }

    val asText: Text
        get() = LiteralText(content)

    fun insert(newStr: String) {
        content = withInserted(newStr)
        cursorPos += newStr.length
    }

    fun withInserted(insertion: String): String {
        if (content.isEmpty()) {
            return insertion
        }
        return "$cursorBefore$insertion$cursorAfter"
    }

    val contentDisplay: String
        get() = withInserted("|")

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area {
            reactWith(reactor)
            rect(0x0)
            dsl {
                getTextLines(contentDisplay).forEachIndexed { i, cText ->
                    text(0, i * 9, cText)
                }
            }
        }
    }
}