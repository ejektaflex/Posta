package io.ejekta.posta.client.gui

import io.ejekta.kambrik.gui.DrawingScope
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.*
import kotlin.math.abs
import kotlin.math.min

open class KTextArea(
    override val width: Int,
    override val height: Int
) : KWidget, ITextBox {

    var content = "hi"

    var lineHeight = 10

    val renderer: TextRenderer
        get() = MinecraftClient.getInstance().textRenderer

    var cursorColor: Int = 0xFFFFFF

    var textColor: Int = 0xFFFFFF

    val keyReactor = KeyReactor().apply {
        onPressDown = { keyCode, scanCode, modifiers ->
            when (keyCode) {
                InputUtil.GLFW_KEY_LEFT -> {
                    if (Screen.hasControlDown()) {
                        moveTo(wordLeft())
                    } else {
                        moveTo(left())
                    }
                }
                InputUtil.GLFW_KEY_RIGHT -> {
                    if (Screen.hasControlDown()) {
                        moveTo(wordRight())
                    } else {
                        moveTo(right())
                    }
                }
                InputUtil.GLFW_KEY_ENTER -> {
                    insert("\n")
                    //cursorPos++
                }
                InputUtil.GLFW_KEY_BACKSPACE -> deleteTo(left())
            }
        }
        onType = { char, modifiers ->
            println("")
            insert(char.toString())
        }
    }

    val virtualString: String
        get() = virtualContent.joinToString("\n")

    val virtualContent: List<String>
        get() = getTextLines(content)

    val virtualLineChar: Pair<Int, Int>
        get() = getLineAndCharIndex(virtualContent)

    override fun start() = 0

    override fun end() = content.length

    override fun left(): Int {
        return (cursorPos - 1).coerceAtLeast(0)
    }

    override fun right(): Int {
        return (cursorPos + 1).coerceAtMost(virtualString.length)
    }

    override fun wordRight(): Int {
        val after = afterCursor(content).split('\n', ' ')
        return cursorPos + (after.firstOrNull()?.length?.coerceAtLeast(1) ?: 0)
    }

    override fun wordLeft(): Int {
        val before = beforeCursor(content).split('\n', ' ').reversed()
        return cursorPos - (before.firstOrNull()?.length?.coerceAtLeast(1) ?: 0)
    }

    override fun moveTo(index: Int) {
        println("Attempting move to: $index")
        val toIndex = index.coerceIn(0..content.length)
        println("Actually moving to: $toIndex")
        cursorPos = toIndex
    }

    override fun deleteTo(index: Int) {
        var toIndex = min(index, content.length)
        if (toIndex !in content.indices) {
            toIndex = cursorPos
        }
        val indexes = listOf(toIndex, cursorPos).sorted()
        content = content.replaceRange(indexes.first(), indexes.last(), "")
        cursorPos = indexes.first()
    }



    val mouseReactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            val lines = getTextLines(content)
            val lineNum = relY / lineHeight
            println("Clicked on TE LineNum: $lineNum")

            // If it's a valid lineNum in our system
            if (lineNum < lines.size) {
                var line = lines[lineNum]
                val trimmed = renderer.trimToWidth(line, relX)


                println("Line: $line (${line.length}) | (${line.trimEnd('\n').length}) | (${trimmed.length})")

                // How much extra to add for current line
                val trimOffset = trimmed.length

                val untrim = if (line.length == trimmed.length) 1 else 0

                // If there's another character after it, pick whichever offset is closer to click pos
//                if (line.length > trimmed.length && !isAtEnd) {
//                    println("BUMPING TRIM OFFSET: ${line.length}")
//                    trimOffset = listOf(trimmed.indices, 0..trimmed.length).map {
//                        line.substring(it)
//                    }.minByOrNull {
//                        abs(relX - renderer.getWidth(it) + 1)
//                    }?.length ?: trimOffset
//                }

                val prevLines = lines.subList(0, lineNum)

                println("Should be going to.. ${prevLines.sumOf { it.length }} w/o offset (off $trimOffset)")

                cursorPos = prevLines.sumOf { it.length } + trimOffset - untrim

                println("Went to $cursorPos.")
            } else {
                moveTo(virtualString.length)
            }
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
        if (str.lastOrNull() == '\n') {
            list.add("")
        }
        return list
    }

    private fun getLineAndCharIndex(lines: List<String>): Pair<Int, Int> {
        var target = cursorPos
        for (i in lines.indices) {
            val line = lines[i]
            if (target >= line.length) {
                target -= line.length
            } else {
                return i to target
            }
        }
        return lines.lastIndex to (lines.lastOrNull()?.length ?: 0)
    }

    private fun getCursorLineAndWidth(): Pair<Int, Int> {
        val allLines = getTextLines(content)

        val res = getLineAndCharIndex(allLines)

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

    private fun beforeCursor(str: String): String {
        return if (cursorPos == 0) {
            ""
        } else {
            str.substring(0 until cursorPos)
        }
    }

    private fun afterCursor(str: String): String {
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

        val supposedLines = getNumLines(potentialContent)

        // Do not do an insertion if this brings it over the line limit
        if (supposedLines > (height / lineHeight)) {
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

    override fun onDraw(area: DrawingScope.AreaScope) {
        area {
            reactWith(keyReactor, mouseReactor)
            rect(0x0) // black bg
            dsl {
                // Draw lines
                getTextLines(content).forEachIndexed { i, cText ->
                    text(0, i * lineHeight, textLiteral(cText.trimEnd()) {
                        color = textColor
                    })
                }
                val location = getCursorLineAndWidth()

                // IF cursor would exceed the box width, move to next line
                var cursorX = location.second
                var cursorY = location.first * lineHeight
                if (cursorX >= width) {
                    cursorX = 0
                    cursorY += lineHeight
                }
                // Draw cursor
                offset(cursorX, cursorY) {
                    rect(1, lineHeight, cursorColor)
                }
            }
        }
    }
}