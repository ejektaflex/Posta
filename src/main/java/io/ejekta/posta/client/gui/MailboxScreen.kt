package io.ejekta.posta.client.gui

import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import io.ejekta.posta.PostaMod
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class MailboxScreen(
    handler: ScreenHandler,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<ScreenHandler>(
    handler, inventory, title
) {

    init {
        backgroundWidth = 176
        backgroundHeight = 246
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        drawSimpleCenteredImage(matrices, TEXTURE, backgroundWidth, backgroundHeight)
    }

    companion object {
        private val TEXTURE = Identifier(PostaMod.ID, "textures/bg.png")
    }

}