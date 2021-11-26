package io.ejekta.posta.client.gui

import io.ejekta.kambrik.KambrikHandledScreen
import io.ejekta.kambrik.ext.client.drawSimpleCenteredImage
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.posta.PostaMod
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
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

    var textField = TextFieldWidget(MinecraftClient.getInstance().textRenderer, 25, 25, 200, 50, textLiteral("Hi!")).apply {
        setEditable(true)
        setMaxLength(50)
    }

    override fun init() {
        addSelectableChild(textField)
        setInitialFocus(textField)
        //addSelectableChild(textTwo)
    }

    override fun handledScreenTick() {
        textField.tick()
    }

    override fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        //TODO("Not yet implemented")
    }

    val textArea = KTextAreaWidget(200, 120)

    val fgGui = kambrikGui {
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
        textField.render(matrices, mouseX, mouseY, delta)
        //textTwo.render(matrices, mouseX, mouseY, delta)
    }

    companion object {
        private val TEXTURE = Identifier(PostaMod.ID, "textures/letter_bg.png")
    }

}