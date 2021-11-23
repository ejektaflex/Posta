package io.ejekta.kambrik.gui.reactor

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

    fun doPress(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (isPressing(keyCode, scanCode, modifiers)) {
            doPressDown(keyCode, scanCode, modifiers)
        }
    }

    fun doPressDown(keyCode: Int, scanCode: Int, modifiers: Int) {
        onPressDown(keyCode, scanCode, modifiers)
    }

}