package io.ejekta.kambrik

import io.ejekta.kambrik.gui.drawing.DrawingDsl
import io.ejekta.kambrik.gui.drawing.KRect
import io.ejekta.kambrik.gui.reactor.EventReactor
import io.ejekta.kambrik.gui.reactor.KeyReactor
import io.ejekta.kambrik.gui.reactor.MouseReactor
import io.ejekta.kambrik.math.Vec2i
import net.minecraft.client.gui.Element
import net.minecraft.client.util.math.MatrixStack

interface KambrikSurface : Element {
    val boundsStack: MutableList<Pair<MouseReactor, KRect>>
    val keyStack: MutableList<KeyReactor>
    val areaClickStack: MutableList<Pair<() -> Unit, KRect>> // Currently unused
    val modalStack: MutableList<DrawingDsl>
    fun onDrawBackground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {}
    fun onDrawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {}

    val focused: MutableList<EventReactor>


    fun isFocused(reactor: EventReactor): Boolean {
        return reactor in focused
    }

    fun focusAlsoOn(reactor: EventReactor) {
        focused.add(reactor)
    }

    fun defocus() {
        focused.clear()
    }

    fun focusSolelyOn(reactor: EventReactor) {
        defocus()
        focusAlsoOn(reactor)
    }

    private fun cycleMouseReactors(func: (reactor: MouseReactor, rect: KRect) -> Unit) {
        for (bounds in boundsStack) {
            func(bounds.first, bounds.second)
        }
    }

    private fun cycleMouseReactorsInBounds(mouseX: Double, mouseY: Double, func: (reactor: MouseReactor, rect: KRect, mVec: Vec2i) -> Unit) {
        for (bounds in boundsStack) {
            if (bounds.second.isInside(mouseX.toInt(), mouseY.toInt())) {
                func(bounds.first, bounds.second, Vec2i(mouseX.toInt(), mouseY.toInt()))
            }
        }
    }

    private fun cycleKeyReactors(func: (reactor: KeyReactor) -> Unit) {
        for (bounds in keyStack) {
            func(bounds)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (bounds in boundsStack) {
            val widget = bounds.first
            val rect = bounds.second
            if (bounds.second.isInside(mouseX.toInt(), mouseY.toInt())) {

                if (widget.canDragStart() && !widget.isDragging) {
                    widget.doDragStart(
                        Vec2i(mouseX.toInt() - rect.pos.x, mouseY.toInt() - rect.pos.y),
                        Vec2i(mouseX.toInt(), mouseY.toInt())
                    )
                }

                widget.doClickDown(Vec2i(mouseX.toInt() - rect.pos.x, mouseY.toInt() - rect.pos.y), button)

                if (!widget.canPassThrough()) {
                    break // If we cannot continue down the bounds stack because there's no clickthrough, return
                }
            }
        }
//        for (clicks in areaClickStack) {
//            if (clicks.second.isInside(mouseX.toInt(), mouseY.toInt())) {
//                clicks.first()
//            }
//        }
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        cycleMouseReactors { mouseReact, rect ->
            if (mouseReact.canDragStop() && mouseReact.isDragging) {
                mouseReact.doDragStop(Vec2i(mouseX.toInt() - rect.pos.x, mouseY.toInt() - rect.pos.y))
            }
        }
        cycleMouseReactorsInBounds(mouseX, mouseY) { widget, rect, mVec ->
            widget.doClickUp(mVec - rect.pos, button)
        }
        return true
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        cycleMouseReactorsInBounds(mouseX, mouseY) { widget, rect, mVec ->
            widget.onMouseMoved(mVec - rect.pos)
        }
        cycleMouseReactors { mouseReact, rect ->
            if (mouseReact.isDragging) {
                mouseReact.doDragging(Vec2i(
                    mouseX.toInt(),
                    mouseY.toInt()
                ))
            }
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        cycleMouseReactorsInBounds(mouseX, mouseY) { widget, rect, mVec ->
            widget.onMouseScrolled(mVec - rect.pos, amount)
        }
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (keyReact in keyStack) {
            keyReact.doPress(keyCode, scanCode, modifiers)
        }
        return true
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        for (keyReact in keyStack) {
            keyReact.doType(chr, modifiers)
        }
        return true
    }

}