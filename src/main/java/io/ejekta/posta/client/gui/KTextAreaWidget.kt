package io.ejekta.posta.client.gui

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
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

    var cursorColor: Int = 0xFFFFFF

    val keyReactor = KeyReactor().apply {
        onPressDown = { keyCode, scanCode, modifiers ->
            when (keyCode) {
                InputUtil.GLFW_KEY_LEFT -> cursorPos--
                InputUtil.GLFW_KEY_RIGHT -> cursorPos++
                InputUtil.GLFW_KEY_BACKSPACE -> deleteOnce()
            }
        }
        onType = { char, modifiers ->
            insert(char.toString())
        }
    }

    val mouseReactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            println("$relX, $relY, ${relY/lineHeight}")
            val lines = getTextLines(content)
            val lineNum = relY / lineHeight
            if (lineNum >= lines.size) {

            } else {
                val line = lines[lineNum]
                val trimmed = renderer.trimToWidth(line, relX)
                val prevLines = lines.subList(0, lineNum)
                cursorPos = prevLines.joinToString("").length + trimmed.length
            }
        }
    }

    private fun getTextLines(str: String): List<String> {
        return renderer.textHandler.wrapLines(str, width, Style.EMPTY).map {
            it.string
        }
    }

    private fun getCursorLineAndWidth(str: String): Pair<Int, Int> {
        val beforeLines = getTextLines(str)
        return beforeLines.size.coerceAtLeast(1) to if (beforeLines.isEmpty()) {
            1
        } else {
            renderer.getWidth(beforeLines.last())
        }
    }

    private fun getNumLines(str: String) = getTextLines(str).size

    var cursorPos: Int = 0
        get() {
            return field.coerceIn(0..content.length)
        }

    private val cursorBefore: String
        get() = if (cursorPos == 0) {
            ""
        } else {
            content.substring(0 until cursorPos)
        }

    val isCursorAtEnd: Boolean
        get() = cursorBefore == content

    private val cursorAfter: String
        get() = if (cursorPos >= content.length) {
            ""
        } else {
            content.substring(cursorPos until content.length)
        }

    val asText: Text
        get() = LiteralText(content)

    private fun deleteOnce() {
        if (cursorBefore.isEmpty()) {
            return
        }
        if (cursorAfter.isEmpty()) {
            content = cursorBefore.dropLast(1)
        } else {
            content = cursorBefore.dropLast(1) + cursorAfter
            cursorPos--
        }
    }

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
            reactWith(mouseReactor)
            rect(0x0) // black bg
            dsl {
                getTextLines(content).forEachIndexed { i, cText ->
                    text(0, i * lineHeight, LiteralText(cText))
                }
                val location = getCursorLineAndWidth(cursorBefore)
                offset(location.second, (location.first - 1) * lineHeight) {
                    rect(1, lineHeight, cursorColor)
                }
            }
        }
    }
}