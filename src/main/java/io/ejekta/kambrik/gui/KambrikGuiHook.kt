package io.ejekta.kambrik.gui

import com.mojang.blaze3d.systems.RenderSystem
import io.ejekta.kambrik.KambrikSurface
import io.ejekta.kambrik.gui.drawing.DrawingDsl
import io.ejekta.kambrik.gui.drawing.DrawingScope
import io.ejekta.kambrik.gui.drawing.KRect
import io.ejekta.kambrik.gui.drawing.KambrikGui
import io.ejekta.kambrik.gui.reactor.EventReactor
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
import io.ejekta.kambrik.math.Vec2i
import net.minecraft.client.gui.screen.Screen

open class KambrikGuiHook<S : Screen> : KambrikSurface {
    override val boundsStack = mutableListOf<Pair<MouseReactor, KRect>>()
    override val keyStack = mutableListOf<KeyReactor>()
    override val areaClickStack = mutableListOf<Pair<() -> Unit, KRect>>()
    override val modalStack = mutableListOf<DrawingDsl>()

    override val focused: MutableList<EventReactor> = mutableListOf()

    fun kambrikGui(clearOnDraw: Boolean = false, func: DrawingScope.() -> Unit) = KambrikGui(
        this, { Vec2i.ZERO }
    ) {
        if (clearOnDraw) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        }
        apply(func)
    }

    open fun appliesTo(screen: Screen): Boolean {
        return false
    }

    fun setToScreen(screen: Screen) {

    }

}