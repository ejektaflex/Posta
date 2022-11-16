package io.ejekta.posta.client.gui

import io.ejekta.kambrik.KambrikHandledScreen
import io.ejekta.kambrik.gui.reactor.MouseReactor
import io.ejekta.kambrik.gui.widgets.text.KTextArea
import io.ejekta.posta.PostaMod
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class LetterScreen(
    handler: ScreenHandler,
    inventory: PlayerInventory,
    title: Text
) : KambrikHandledScreen<ScreenHandler>(
    handler, inventory, title
) {

    override fun init() { /* No-op */ }

    private val TEXTURE = Identifier(PostaMod.ID, "textures/letter_bg.png")

    val textArea = KTextArea(160, 110)

    val m = MouseReactor().apply {
        onDragStart = { _ ->
            println("Drag started")
        }
        onDragEnd = { _ ->
            println("Drag ended")
        }
    }

    val kgui = kambrikGui {

        offset(10, 10) {
            offset(m.dragPos) {
                area(25, 25) {
                    rect(Formatting.RED.colorValue!!)
                    reactWith(m)
                }
            }
        }

        areaCenteredInScreen(textArea.width, 0) {
            offset(0, -backgroundHeight / 2 + 5) {
                widget(textArea)
            }
            textNoShadow(0, -5) {
                addLiteral("POS: ${textArea.virtualLineChar} C: ${textArea.cursorPos}, VSL: ${textArea.virtualString.length}, SL: ${textArea.content.length}")
                format(Formatting.BLACK)
            }
            textNoShadow(0, 5) {
                addLiteral("CL&W: ${textArea.getCursorLineAndWidth()}")
                format(Formatting.BLACK)
            }
        }

    }

    init {
        backgroundWidth = 176
        backgroundHeight = 246
        kgui.screen = this
    }

    override fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        kgui.draw(matrices, mouseX, mouseY, delta)
    }

}