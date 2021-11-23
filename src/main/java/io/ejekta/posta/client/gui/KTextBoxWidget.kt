package io.ejekta.posta.client.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.OrderedText
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

class KTextBoxWidget(
    textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: Text
) : ClickableWidget(x, y, width, height, text), Drawable, Element {

    var focusColor = 0x000000

    var unfocusColor = 0x333333

    var textString = "blah"

    var cursorPos = textString.length

    val texter: TextRenderer
        get() = MinecraftClient.getInstance().textRenderer

    val blinkCursor: String
        get() = if ((MinecraftClient.getInstance().world?.time ?: 0L) % 16L >= 8L) "_" else " "

    fun getStringRenderLook(insert: String, delAmt: Int = 0): String {
        return textString
            .substring(
                0 until (cursorPos - delAmt).coerceAtLeast(1)
            ) + insert + textString.substring(
            cursorPos - delAmt until textString.length.coerceAtLeast(1)
            )
    }

    val textStringLines: List<OrderedText>
        get() = texter.wrapLines(
            StringVisitable.plain(
                getStringRenderLook(blinkCursor)
            ),
            width
        )

    fun moveCursor(amt: Int) {
        cursorPos = (cursorPos + amt).coerceIn(0..textString.length)
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (visible) {
            val focusingColor = if (isFocused) focusColor else unfocusColor
            fill(matrices, x - 1, y - 1, x + width + 1, y + height + 1, focusingColor)
            fill(matrices, x, y, x + width, y + height, -16777216)

            textStringLines.forEachIndexed { i, orderedText ->
                texter.draw(matrices, orderedText, x + 1f, y + 1f + (16 * i).toFloat(), 0xFFFFFF)
            }

        } else {
            println("Not visible")
        }
    }

    override fun onFocusedChanged(newFocused: Boolean) {
        super.onFocusedChanged(newFocused)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        textString = getStringRenderLook(chr.toString())
        moveCursor(1)
        return super<ClickableWidget>.charTyped(chr, modifiers)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!visible) {
            return false
        }

        val anX = (mouseX - x).toInt()
        val pos = texter.trimToWidth(getStringRenderLook(blinkCursor), anX).length
        cursorPos = pos

        return super<ClickableWidget>.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_BACKSPACE -> {
                println("Pressed: delete!")
                textString = textString.take((cursorPos - 1).coerceAtLeast(0)) + textString.takeLast((textString.length - cursorPos).coerceAtLeast(0))
                moveCursor(-1)
            }
            GLFW.GLFW_KEY_RIGHT -> moveCursor(1)
            GLFW.GLFW_KEY_LEFT -> moveCursor(-1)
        }
        return super<ClickableWidget>.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {
        // skip for now
    }


}