package io.ejekta.posta.blocks

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class MailboxBlock : BlockWithEntity(
    FabricBlockSettings.of(Material.METAL).hardness(1f).resistance(3600000f)
) {

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return MailboxBlockEntity(pos, state)
    }

}