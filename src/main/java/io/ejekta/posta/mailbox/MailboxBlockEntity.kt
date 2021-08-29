package io.ejekta.posta.mailbox

import io.ejekta.kambrik.text.textTranslate
import io.ejekta.posta.PostaContent
import io.ejekta.posta.mailbox.MailboxScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

class MailboxBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(PostaContent.MAILBOX_BLOCK_ENTITY, pos, state), ExtendedScreenHandlerFactory {

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        return MailboxScreenHandler(syncId, inv, MailboxInventory())
    }

    override fun getDisplayName() = textTranslate("block.posta.mailbox")

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {

    }

}