package io.ejekta.posta.letter

import io.ejekta.posta.PostaContent
import io.ejekta.posta.mailbox.KambrikScreenHandler
import io.ejekta.posta.mailbox.MailboxInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.slot.Slot

class LetterScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    override var inventory: Inventory
) : KambrikScreenHandler<LetterScreenHandler>(PostaContent.LETTER_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(syncId, playerInventory, MailboxInventory())

    override fun canUse(player: PlayerEntity) = true

    init {
        makePlayerDefaultGrid(playerInventory, 8, 140)
    }

}