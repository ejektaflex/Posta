package io.ejekta.kambrik

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.gui.KGui
import io.ejekta.kambrik.gui.KGuiDsl
import io.ejekta.kambrik.gui.KRect
import io.ejekta.kambrik.gui.reactor.EventReactor
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

abstract class KambrikScreen(title: Text) : Screen(title), KambrikSurface {
    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val keyStack = mutableListOf<KeyReactor>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<KGuiDsl.() -> Unit>()

    override val focused: MutableList<EventReactor> = mutableListOf()

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikSurface>.mouseClicked(mouseX, mouseY, button)
        return super<Screen>.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super<KambrikSurface>.mouseReleased(mouseX, mouseY, button)
        return super<Screen>.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        super<KambrikSurface>.mouseMoved(mouseX, mouseY)
        super<Screen>.mouseMoved(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        super<KambrikSurface>.mouseScrolled(mouseX, mouseY, amount)
        return super<Screen>.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        super<KambrikSurface>.keyPressed(keyCode, scanCode, modifiers)
        return super<Screen>.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        onDrawBackground(matrices, mouseX, mouseY, delta)
        super.render(matrices, mouseX, mouseY, delta)
        onDrawForeground(matrices, mouseX, mouseY, delta)
    }

    fun kambrikGui(clearOnDraw: Boolean = false, func: KGuiDsl.() -> Unit) = KGui(
        this, { 0 to 0 }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }

}