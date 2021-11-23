package io.ejekta.kambrik.gui

import io.ejekta.kambrik.text.textLiteral

interface KWidget {

    val width: Int

    val height: Int

    fun doDraw(dsl: KGuiDsl) {
        dsl {
            area(width, height) {
                onDraw(this)
                if (drawDebug && isHovered) {
                    rect(0x4287f5, 0x33)
                    text(0, -9, textLiteral(
                        "${this@KWidget::class.simpleName ?: "???"} ($width x $height)"
                    ) {
                        color = 0x4287f5
                    })
                }
            }
        }
    }

    /**
     * A callback that allows the widget to draw to the screen.
     */
    fun onDraw(area: KGuiDsl.AreaDsl) {
        /* No-op
        return dsl {
            ...
        }
         */
    }
}