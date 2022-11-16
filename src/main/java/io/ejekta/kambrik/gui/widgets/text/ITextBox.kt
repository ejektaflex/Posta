package io.ejekta.kambrik.gui.widgets.text

import io.ejekta.kambrik.gui.reactor.MouseReactor

/*
    Should closely follow RXI's text box behaviour
    https://rxi.github.io/textbox_behaviour.html
 */
interface ITextBox {

    fun start(): Int

    fun end(): Int

    fun left(): Int

    fun right(): Int

    fun wordRight(): Int

    fun wordLeft(): Int

    fun moveTo(index: Int) {

    }

    fun deleteTo(index: Int) {

    }

}