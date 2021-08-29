package io.ejekta.posta

import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.posta.letter.LetterItem
import io.ejekta.posta.letter.LetterScreenHandler
import io.ejekta.posta.mailbox.MailboxBlock
import io.ejekta.posta.mailbox.MailboxBlockEntity
import io.ejekta.posta.mailbox.MailboxScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object PostaContent : KambrikAutoRegistrar {

    override fun manualRegister() { /* nah */ }

    val LETTER = "letter" forItem LetterItem()

    val MAILBOX_BLOCK = "mailbox" forBlock MailboxBlock()

    val MAILBOX_ITEM = "mailbox" forItem BlockItem(MAILBOX_BLOCK, Item.Settings().group(ItemGroup.MISC))

    val MAILBOX_BLOCK_ENTITY = "mailbox_block_entity".forBlockEntity(MAILBOX_BLOCK) { pos, state ->
        MailboxBlockEntity(pos, state)
    }

    val MAILBOX_SCREEN_HANDLER = forExtendedScreen(
        Identifier(PostaMod.ID, "mailbox_screen"),
        ::MailboxScreenHandler
    )

    val LETTER_SCREEN_HANDLER = forExtendedScreen(
        Identifier(PostaMod.ID, "letter_screen"),
        ::LetterScreenHandler
    )

    fun <T : ScreenHandler> forScreen(id: Identifier, factory: ScreenHandlerRegistry.SimpleClientHandlerFactory<T>): ScreenHandlerType<T>? {
        return ScreenHandlerRegistry.registerSimple(id, factory)
    }


}