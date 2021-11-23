package io.ejekta.posta.mailbox

import io.ejekta.kambrik.KambrikScreenHandler
import io.ejekta.posta.PostaContent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.slot.Slot

class MailboxScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    override var inventory: Inventory
) : KambrikScreenHandler<MailboxScreenHandler, Inventory>(PostaContent.MAILBOX_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(syncId, playerInventory, MailboxInventory())

    override fun canUse(player: PlayerEntity) = true

    init {
        makePlayerDefaultGrid(playerInventory, 8, 140)
    }

}