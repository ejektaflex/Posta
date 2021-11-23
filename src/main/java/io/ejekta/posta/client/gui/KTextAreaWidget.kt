package io.ejekta.posta.client.gui

import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KWidget
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.text.textLiteral
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Formatting

class KTextAreaWidget(override val width: Int, override val height: Int) : KWidget {

    var content = "hi"

    var cursorPos: Int = 0

    val reactor = KeyReactor().apply {
        onPressDown = { keyCode, scanCode, modifiers ->
            println("Pressed for box: $keyCode, $scanCode, $modifiers")
            println(Screen.hasAltDown())
            println("Char: ${keyCode.toChar()}, ${scanCode.toChar()}")
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

    fun insert(char: Char) {

    }

    val safeCursorPos: Int
        get() = cursorPos.coerceAtMost(content.length)

    val contentDisplay: String
        get() {
            if (content.isEmpty()) {
                return "|"
            }
            return "$cursorBefore|$cursorAfter"
        }

    override fun onDraw(area: KGuiDsl.AreaDsl) {
        area {
            reactWith(reactor)
            rect(0x0)
            dsl {
                text(0, 0, textLiteral(contentDisplay) {
                    format(Formatting.GOLD)
                })
            }
        }
    }
}