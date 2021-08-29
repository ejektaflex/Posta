package io.ejekta.posta.mailbox

import io.ejekta.kambrik.text.textLiteral
import io.ejekta.posta.mailbox.MailboxBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MailboxBlock : BlockWithEntity(
    FabricBlockSettings.of(Material.METAL).hardness(1f).resistance(3600000f)
), BlockEntityProvider {

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return MailboxBlockEntity(pos, state)
    }

    override fun onUse(
        state: BlockState,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult?
    ): ActionResult {

        val screenHandlerFactory = state.createScreenHandlerFactory(world, pos)

        screenHandlerFactory?.let {
            player.openHandledScreen(it)
            return ActionResult.SUCCESS
        }

        return ActionResult.FAIL
    }

}