package io.ejekta.kambrik.gui

import io.ejekta.kambrik.KambrikScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack


/**
 * Accessed via [Kambrik.Gui][io.ejekta.kambrik.Kambrik.Gui]
 */
internal open class KambrikGuiApi internal constructor() {

    val screenHooks = mutableListOf<KambrikGuiHook<*>>()

    fun addHook(guiHook: KambrikGuiHook<*>) {
        screenHooks.add(guiHook)
    }

    fun handleScreenRendering(screen: Screen, ms: MatrixStack, mouseX: Int, mouseY: Int, delta: Float? = null) {
        for (hook in screenHooks) {
            if (screen !is KambrikScreen && hook.appliesTo(screen)) {
                hook.setToScreen(screen)
            }
        }
    }

    companion object API : KambrikGuiApi() {
    }

}