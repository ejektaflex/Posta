package io.ejekta.kambrik.math

data class Vec2i(val x: Int, val y: Int) {
    operator fun times(n: Int) = Vec2i(x * n, y * n)

    operator fun plus(other: Vec2i) = Vec2i(x + other.x, y + other.y)

    operator fun minus(other: Vec2i) = Vec2i(x - other.x, y - other.y)

    companion object {
        val ZERO = Vec2i(0, 0)
    }
}