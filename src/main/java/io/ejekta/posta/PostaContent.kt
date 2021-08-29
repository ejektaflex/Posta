package io.ejekta.posta

import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import io.ejekta.posta.blocks.MailboxBlock
import io.ejekta.posta.blocks.MailboxBlockEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

object PostaContent : KambrikAutoRegistrar {

    override fun manualRegister() { /* nah */ }

    val MAILBOX_BLOCK = "mailbox" forBlock MailboxBlock()

    val MAILBOX_ITEM = "mailbox" forItem BlockItem(MAILBOX_BLOCK, Item.Settings().group(ItemGroup.MISC))

    val MAILBOX_BLOCK_ENTITY = "mailbox_block_entity".forBlockEntity(MAILBOX_BLOCK) { pos, state ->
        MailboxBlockEntity(pos, state)
    }

}