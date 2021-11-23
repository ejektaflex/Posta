package io.ejekta.kambrik.gui.reactor

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil

open class KeyReactor(
    defaultCanPass: Boolean = false,
) : EventReactor(defaultCanPass) {

    operator fun invoke(func: KeyReactor.() -> Unit) = apply(func)


    var isPressing: (keyCode: Int, scanCode: Int, modifiers: Int) -> Boolean = { _, _, _ ->
        true
    }

    var onPressDown: (keyCode: Int, scanCode: Int, modifiers: Int) -> Unit = { _, _, _ ->
        // No-op
    }

    var onType: (char: Char, modifiers: Int) -> Unit = { _, _ ->
        // No-op
    }

    var onCut: () -> Unit = { /* No-op */ }

    var onCopy: () -> Unit = { /* No-op */ }

    var onPaste: () -> Unit = { /* No-op */ }

    fun doPress(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (isPressing(keyCode, scanCode, modifiers)) {
            doPressDown(keyCode, scanCode, modifiers)
            if (Screen.isCopy(keyCode)) {
                onCopy()
            }
            if (Screen.isPaste(keyCode)) {
                onPaste()
            }
            if (Screen.isCut(keyCode)) {
                onCut()
            }
        }
    }

    fun doType(char: Char, modifiers: Int) {
        onType(char, modifiers)
    }

    fun doPressDown(keyCode: Int, scanCode: Int, modifiers: Int) {
        onPressDown(keyCode, scanCode, modifiers)
    }

}