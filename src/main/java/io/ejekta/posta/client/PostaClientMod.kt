package io.ejekta.posta.client

import io.ejekta.kambrik.gui.KambrikGuiApi
import io.ejekta.posta.PostaContent
import io.ejekta.posta.client.gui.LetterScreen
import io.ejekta.posta.client.gui.MailboxScreen
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screen.ingame.HandledScreens

class PostaClientMod : ClientModInitializer {

    override fun onInitializeClient() {

        HandledScreens.register(PostaContent.MAILBOX_SCREEN_HANDLER, ::MailboxScreen)
        HandledScreens.register(PostaContent.LETTER_SCREEN_HANDLER, ::LetterScreen)

        // Kambrik Letter GUI

        //KambrikGuiApi.addHook()

    }

}