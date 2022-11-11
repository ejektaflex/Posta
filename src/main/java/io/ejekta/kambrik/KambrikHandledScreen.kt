package io.ejekta.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.gui.KSpriteGrid
import io.ejekta.kambrik.gui.KambrikGui
import io.ejekta.kambrik.gui.KRect
import io.ejekta.kambrik.gui.reactor.MouseReactor
import io.ejekta.kambrik.gui.DrawingScope
import io.ejekta.kambrik.gui.reactor.EventReactor
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.math.Vec2i
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

abstract class KambrikHandledScreen<SH : ScreenHandler>(
    handler: SH,
    inventory: PlayerInventory,
    title: Text
) : HandledScreen<SH>(handler, inventory, title), KambrikSurface {

    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val keyStack = mutableListOf<KeyReactor>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<DrawingScope.() -> Unit>()

    override val focused: MutableList<EventReactor> = mutableListOf()

    fun sizeToSprite(sprite: KSpriteGrid.Sprite) {
        backgroundWidth = sprite.width
        backgroundHeight = sprite.height
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) { /* Pass here */ }
    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        onDrawBackground(matrices, mouseX, mouseY, delta)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        onDrawForeground(matrices, mouseX, mouseY, delta)
        drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikSurface>.mouseClicked(mouseX, mouseY, button)
        return super<HandledScreen>.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikSurface>.mouseReleased(mouseX, mouseY, button)
        return super<HandledScreen>.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikSurface>.mouseMoved(mouseX, mouseY)
        super<HandledScreen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        super<KambrikSurface>.mouseScrolled(mouseX, mouseY, amount)
        return super<HandledScreen>.mouseScrolled(mouseX, mouseY, amount)
    }


    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        super<KambrikSurface>.charTyped(chr, modifiers)
        return super<HandledScreen>.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        super<KambrikSurface>.keyPressed(keyCode, scanCode, modifiers)
        return super<HandledScreen>.keyPressed(keyCode, scanCode, modifiers)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: DrawingScope.() -> Unit) = KambrikGui(
        this, { Vec2i(x, y) }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }


}