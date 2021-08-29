package io.ejekta.posta.mailbox

import io.ejekta.posta.PostaContent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class MailboxScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    val inventory: Inventory
) : ScreenHandler(PostaContent.MAILBOX_SCREEN_HANDLER, syncId) {

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(syncId, playerInventory, MailboxInventory())

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    override fun transferSlot(player: PlayerEntity, invSlot: Int): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot: Slot? = slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val originalStack: ItemStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY
            }
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return newStack
    }

    fun <I : Inventory, S : Slot> makeSlotGrid(
        inventory: I,
        cols: Int,
        rows: Int,
        offX: Int = 0,
        offY: Int = 0,
        padding: Int = 0,
        startIndex: Int = 0,
        slotMaker: ( (inv: I, index: Int, x: Int, y: Int) -> S )? = null
    ) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val calcIndex = col + row * cols + startIndex
                val calcX = col * (18 + padding) + offX
                val calcY = row * (18 + padding) + offY
                println("IND: $calcIndex, X: $calcX, Y: $calcY")
                if (slotMaker != null) {
                    addSlot(slotMaker(inventory, calcIndex, calcX, calcY))
                } else {
                    addSlot(Slot(inventory, calcIndex, calcX, calcY))
                }
            }
        }
    }

    private fun makePlayerInventoryGrid(playerInventory: PlayerInventory, offX: Int, offY: Int) {
        makeSlotGrid<PlayerInventory, Slot>(playerInventory, 9, 3, offX, offY, startIndex = 9)
    }

    private fun makePlayerHotbarGrid(playerInventory: PlayerInventory, offX: Int, offY: Int) {
        makeSlotGrid<PlayerInventory, Slot>(playerInventory, 9, 1, offX, offY)
    }

    private fun makePlayerDefaultGrid(playerInventory: PlayerInventory, offX: Int, offY: Int) {
        makePlayerInventoryGrid(playerInventory, offX, offY)
        makePlayerHotbarGrid(playerInventory, offX, offY + 58)
    }

    init {
        checkSize(inventory, 0)

        makePlayerDefaultGrid(playerInventory, 8, 140)
    }

}