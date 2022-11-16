package io.ejekta.posta

import io.ejekta.kambrik.ext.register
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.posta.letter.LetterItem
import io.ejekta.posta.letter.LetterScreenHandler
import io.ejekta.posta.mailbox.MailboxBlock
import io.ejekta.posta.mailbox.MailboxBlockEntity
import io.ejekta.posta.mailbox.MailboxScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object PostaContent : KambrikAutoRegistrar {

    val LETTER = "letter" forItem LetterItem()

    val MAILBOX_BLOCK = "mailbox" forBlock MailboxBlock()

    val MAILBOX_ITEM = "mailbox" forItem BlockItem(MAILBOX_BLOCK, Item.Settings().group(ItemGroup.MISC))

    val MAILBOX_BLOCK_ENTITY = "mailbox_block_entity".forBlockEntity(MAILBOX_BLOCK) { pos, state ->
        MailboxBlockEntity(pos, state)
    }

    val MAILBOX_SCREEN_HANDLER = "mailbox_screen_handler" forExtendedScreen(
        ::MailboxScreenHandler
    )

    val LETTER_SCREEN_HANDLER = "letter_screen_handler" forExtendedScreen(
        ::LetterScreenHandler
    )


}