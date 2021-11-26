package io.ejekta.kambrik.gui

import io.ejekta.kambrik.KambrikSurface
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity


class KambrikGui(
    val screen: Screen,
    private val coordFunc: () -> Pair<Int, Int>,
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

    fun pushModal(dsl: DrawingScope.() -> Unit) = logic.modalStack.add(dsl)

    fun absX(relX: Int = 0) = x + coordFunc().first + relX

    fun absY(relY: Int = 0) = y + coordFunc().second + relY

}