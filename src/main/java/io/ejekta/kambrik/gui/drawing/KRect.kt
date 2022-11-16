package io.ejekta.kambrik.gui.drawing

import io.ejekta.kambrik.math.Vec2i

data class KRect(val pos: Vec2i, val size: Vec2i) {
    fun isInside(ix: Int, iy: Int): Boolean {
        return isInside(ix, iy, pos.x, pos.y, size.x, size.y)
    }



    companion object {
        fun isInside(ix: Int, iy: Int, x: Int, y: Int, w: Int, h: Int): Boolean {
            return ix >= x && ix < x + w
                    && iy >= y && iy < y + h
        }
    }
}