package io.ejekta.posta.blocks

import io.ejekta.posta.PostaContent
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class MailboxBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(PostaContent.MAILBOX_BLOCK_ENTITY, pos, state) {



}