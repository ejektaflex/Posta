package io.ejekta.posta.client.gui

import io.ejekta.kambrik.KambrikHandledScreen
import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import io.ejekta.kambrik.gui.reactor.MouseReactor
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

    init {
        backgroundWidth = 176
        backgroundHeight = 246
    }

    override fun init() {

    }

    override fun handledScreenTick() {
        //
    }

    override fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        //TODO("Not yet implemented")
    }

    val textArea = KTextArea(200, 120)

    val m = MouseReactor().apply {
        onDragStart = { _, _ ->
            println("Drag started")
        }
        onDragging = { x, y ->
            println("Drag! $x $y $dragPos")
        }
        onDragModify = { x, y ->
            x to 0
        }
        onDragEnd = { _, _ ->
            println("Drag ended")
        }
    }

    val fgGui = kambrikGui {

        offset(10, 10) {
            offset(m.dragPos.first, m.dragPos.second) {
                area(25, 25) {
                    rect(Formatting.RED.colorValue!!)
                    reactWith(m)
                }
            }
        }

        offset(100, 100) {
            widget(textArea)
        }
    }

    override fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        fgGui.draw(matrices, mouseX, mouseY, delta)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        drawSimpleCenteredImage(matrices, TEXTURE, backgroundWidth, backgroundHeight)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        //textTwo.render(matrices, mouseX, mouseY, delta)
    }

    companion object {
        private val TEXTURE = Identifier(PostaMod.ID, "textures/letter_bg.png")
    }

}