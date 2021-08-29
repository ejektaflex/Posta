package io.ejekta.posta.client

import io.ejekta.posta.PostaContent
import io.ejekta.posta.client.gui.MailboxScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry

class PostaClientMod : ClientModInitializer {

    override fun onInitializeClient() {

        ScreenRegistry.register(PostaContent.MAILBOX_SCREEN_HANDLER, ::MailboxScreen)

    }

}