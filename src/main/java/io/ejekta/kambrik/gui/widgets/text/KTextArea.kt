package io.ejekta.kambrik.gui.widgets.text

import io.ejekta.kambrik.gui.drawing.DrawingScope
import io.ejekta.kambrik.gui.widgets.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
import io.ejekta.kambrik.math.Vec2i
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

    val virtualString: String
        get() = virtualContent.joinToString("\n")

    val virtualContent: List<String>
        get() = getTextLines(content)

    val virtualLineChar: Pair<Int, Int>
        get() = getLineAndCharIndex(cursorPos, virtualContent)

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

    private fun getLineAndCharIndex(fromPos: Int, lines: List<String>): Pair<Int, Int> {
        var target = fromPos
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

        val res = getLineAndCharIndex(cursorPos, allLines)

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

    private var dragStartRel = Vec2i.ZERO
    private var dragEndRel = Vec2i.ZERO

    // Gets the position of the caret from a local vector
    fun getCaretPosition(relVec: Vec2i): Int {
        var returnCaret = 0
        val lines = getTextLines(content)
        val lineNum = relVec.y / lineHeight

        // If it's a valid lineNum in our system
        if (lineNum < lines.size) {
            val line = lines[lineNum]
            val nextLine = lines.getOrNull(lineNum + 1)
            val isSoftwrapped = !line.endsWith('\n') && nextLine != null
            val trimmed = renderer.trimToWidth(line, relVec.x)

            returnCaret = lines.subList(0, lineNum).sumOf { it.length } // move to start of clicked line

            // if past the end of the visual line
            if (renderer.getWidth(line.trimEnd('\n')) <= relVec.x) {
                returnCaret += line.trimEnd('\n').length + (if (isSoftwrapped) -1 else 0)
            } else {
                returnCaret += trimmed.length // otherwise just move to the end of string where cursor was
                // Implement "nudging" - if clicked on the right side of a character, nudge the cursor right
                val doot = getLineAndCharIndex(returnCaret, lines)
                val nextChar = line.getOrNull(doot.second)
                val charSize = nextChar?.let { renderer.getWidth(it.toString()) }
                charSize?.let {
                    val currentCaretWidth = renderer.getWidth(line.substring(0 until doot.second))
                    // If over halfway clicked through the char, go forward one char
                    println("Ret: $returnCaret")
                    if (relVec.x - currentCaretWidth > it / 2) {
                        returnCaret++
                    }
                    println("Char: $nextChar, Size: $it, Clicked At: ${relVec.x}, Trimmed to: ${renderer.getWidth(line.substring(0 until doot.second))}")
                }
            }
        } else {
            returnCaret = virtualString.length
        }

        return returnCaret
    }

    val mouseReactor = MouseReactor().apply {

        // Placing caret
        onClickDown = { relVec, button ->
            moveTo(getCaretPosition(relVec))
            dragStartRel = relVec
        }

        // Dragging
        onDragging = { lastDragDelta, totalDragDelta ->
            dragEndRel = totalDragDelta
        }

        onDragEnd = { relVec ->
            println("We ended the drag here: $relVec")
            dragStartRel = Vec2i.ZERO
            dragEndRel = Vec2i.ZERO
        }

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

                offset(dragStartRel) {
                    rect(dragEndRel.x, dragEndRel.y, color = 0x88FFFF)
                }

            }
        }
    }
}