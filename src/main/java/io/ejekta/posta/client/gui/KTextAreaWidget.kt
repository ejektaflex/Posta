package io.ejekta.posta.client.gui

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextHandler.LineWrappingConsumer
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.InputUtil
import net.minecraft.text.*
import kotlin.random.Random

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
                InputUtil.GLFW_KEY_ENTER -> {
                    content += "\n"
                    cursorPos++
                }
                InputUtil.GLFW_KEY_BACKSPACE -> deleteOnce()
            }
        }
        onType = { char, modifiers ->
            insert(char.toString())
        }
    }

    val mouseReactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
//            println("Content: `$content`")
//            println("LH ${relY/lineHeight}")
//            val lines = getTextLines(content)
//            val lineNum = relY / lineHeight
//            if (lineNum < lines.size) {
//                val line = lines[lineNum]
//                val trimmed = renderer.trimToWidth(line, relX)
//                println("$lines - Trimmed: `$trimmed`")
//                val prevLines = lines.subList(0, lineNum)
//                println("Prev: `$prevLines`")
//                cursorPos = prevLines.map { it.length + 1 }.sum() + trimmed.length
//            }
        }
    }

    private fun getTextLines(str: String): List<String> {
        val list = mutableListOf<String>()
        renderer.textHandler.wrapLines(
            str,
            width,
            Style.EMPTY,
            true
        ) { style: Style?, start: Int, end: Int ->
            list.add(
                StringVisitable.styled(str.substring(start, end), style).string
            )
        }
        return list
//        return str.split("\n").map { splitStr ->
//
//        }.flatten()
    }

    private fun getLine(lines: List<String>): Pair<Int, Int> {
        var target = cursorPos
        val mapped = lines.mapIndexed { li, str -> str.mapIndexed { ci, chr -> Triple(li, ci, chr) } }.flatten()
        val picked = mapped.getOrNull(cursorPos) ?: Triple(lines.size - 1, 0, 'a')
        return picked.first to picked.second
    }

    private fun doot(lines: List<String>): Pair<Int, Int> {
        var target = cursorPos
        for (i in lines.indices) {
            val line = lines[i]
            if (target > line.length) {
                target -= line.length
            } else {
                return i to target
            }
        }
        return lines.lastIndex to (lines.lastOrNull()?.length ?: 0)
    }

    private fun getCursorLineAndWidth(): Pair<Int, Int> {
        val allLines = getTextLines(content)

        val beforeAmounts = allLines.sumOf { it.length }

        val res = doot(allLines)

        if (Random.nextDouble() < 0.02) {
            println("DOOT: $allLines, $beforeAmounts $cursorPos, T${res.first},${res.second}")
            //println("$lineChs into ${allLines[lineNum]}")
        }

        if (res.first in allLines.indices) {
            return if (res.second != 0) {
                res.first to renderer.getWidth(allLines[res.first].substring(0 until res.second))
            } else {
                res.first to 0
            }
        }

        return 0 to 0
    }

    private fun getNumLines(str: String) = getTextLines(str).size

    var cursorPos: Int = 0
        get() {
            return field.coerceIn(0..content.length)
        }

    fun beforeCursor(str: String): String {
        return if (cursorPos == 0) {
            ""
        } else {
            str.substring(0 until cursorPos)
        }
    }

    fun afterCursor(str: String): String {
        return if (cursorPos >= str.length) {
            ""
        } else {
            str.substring(cursorPos until str.length)
        }
    }

    private fun deleteOnce() {
        if (beforeCursor(content).isEmpty()) {
            return
        }
        if (afterCursor(content).isEmpty()) {
            content = beforeCursor(content).dropLast(1)
        } else {
            content = beforeCursor(content).dropLast(1) + afterCursor(content)
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
        return "${beforeCursor(content)}$insertion${afterCursor(content)}"
    }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area {
            reactWith(keyReactor)
            reactWith(mouseReactor)
            rect(0x0) // black bg
            dsl {
                getTextLines(content).forEachIndexed { i, cText ->
                    text(0, i * lineHeight, LiteralText(cText.trimEnd()))
                }
                val location = getCursorLineAndWidth()
                offset(location.second, location.first * lineHeight) {
                    rect(1, lineHeight, cursorColor)
                }
            }
        }
    }
}