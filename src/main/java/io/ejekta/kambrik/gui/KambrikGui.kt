package io.ejekta.kambrik.gui

import io.ejekta.kambrik.KambrikSurface
import io.ejekta.kambrik.math.Vec2i
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity


class KambrikGui(
    val screen: Screen,
    private val coordFunc: () -> Vec2i,
    var x: Int = 0,
    var y: Int = 0,
    private val func: DrawingScope.() -> Unit = {}
) {

    val logic: KambrikSurface
        get() = screen as KambrikSurface

    val entityRenderCache = mutableMapOf<EntityType<*>, LivingEntity>()

    fun draw(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float? = null) {
        logic.boundsStack.clear()
        logic.areaClickStack.clear()
        logic.keyStack.clear()
        val toDraw = logic.modalStack.lastOrNull() ?: func // Draw top of modal stack, or func if not exists
        val dsl = DrawingScope(this, matrices, mouseX, mouseY, delta, true).draw(toDraw)
    }

    //fun pushModal(dsl: DrawingScope.() -> Unit) = logic.modalStack.add(dsl)

    fun absX(relX: Int = 0) = x + coordFunc().x + relX

    fun absY(relY: Int = 0) = y + coordFunc().y + relY

    fun absVec(relVec: Vec2i = Vec2i.ZERO): Vec2i {
        val calc = coordFunc()
        return Vec2i(x + relVec.x + calc.x, y + relVec.y + calc.y)
    }

}