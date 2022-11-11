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
                InputUtil.GLFW_KEY_DELETE -> deleteTo(right())
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

    fun charAt(index: Int): Char {
        return content[index]
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

    // Gets a cursor position from a line number and
//    fun cursorPosFrom(pair: Pair<Int, Int>): Int {
//
//    }

    val mouseReactor = MouseReactor().apply {
        onClickDown = { relX, relY, button ->
            val lines = getTextLines(content)
            val lineNum = relY / lineHeight
            println("Clicked on TE LineNum: $lineNum")

            // If it's a valid lineNum in our system
            if (lineNum < lines.size) {
                val line = lines[lineNum]
                val nextLine = lines.getOrNull(lineNum + 1)
                val isOverflowOrEnd = !line.endsWith('\n')
                val isEnd = nextLine == null
                val trimmed = renderer.trimToWidth(line, relX)

                println("Line: $line, Trimmed: $trimmed")

                val prevLinesAmount = lines.subList(0, lineNum).sumOf { it.length }
                var currLineAmount = trimmed.length


                if (renderer.getWidth(line.trimEnd('\n')) <= relX) {
                    println("We're past the end of the line bro")
                    currLineAmount = line.trimEnd('\n').length
                }

                println("ISOVERFLOW: $isOverflowOrEnd")

                if (isOverflowOrEnd && !isEnd) {
                    cursorPos = prevLinesAmount + currLineAmount - 1
                } else {
                    cursorPos = prevLinesAmount + currLineAmount
                }


                // How much extra to add for current line
//                val trimOffset = trimmed.length
//
//                val isUntrim = line.length == trimmed.length && lineNum != lines.size - 1
//
//                val prevLines = lines.subList(0, lineNum)
//
//                val thisLineLength = trimOffset - (if (isUntrim) 1 else 0)
//
//                cursorPos = prevLines.sumOf { it.length } + thisLineLength
//
//                val lci = getLineAndCharIndex(lines).second

                // Char nudging - clicking 2/3 of the way on the next char moves the cursor an additional spot

                // Somehow this works for all but the very last character
//                val nextChar = line.getOrNull(lci)?.takeIf { lci < line.length - 1 }
//
//                nextChar?.let {
//                    val nextCharSize = renderer.getWidth(it.toString())
//                    println("Next char ($it) size: $nextCharSize")
//                    println("My rel: $relX")
//                    val thisLineText = renderer.getWidth(line.substring(0 until thisLineLength))
//                    println("My line: $thisLineText")
//                    val wDiff = relX - thisLineText
//                    if (wDiff >= nextCharSize / 2) {
//                        cursorPos++
//                    }
//                }
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
        // Newline is by default clipped? This reinserts a new blank like for it
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

    fun getCursorLineAndWidth(): Pair<Int, Int> {
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