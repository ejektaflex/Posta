package io.ejekta.posta.letter

import io.ejekta.kambrik.text.textLiteral
import io.ejekta.posta.PostaContent
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class LetterItem : Item(
    FabricItemSettings().fireproof().maxCount(1)
) {

    class ScreenFact(stack: ItemStack) : ExtendedScreenHandlerFactory {
        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
            return LetterScreenHandler(syncId, inv, SimpleInventory())
        }

        override fun getDisplayName(): Text {
            return textLiteral("Hi!")
        }

        override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
            // nothing to write right now
        }
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {

        val inHand = user.getStackInHand(hand)
        user.openHandledScreen(ScreenFact(inHand))

        return super.use(world, user, hand)
    }

}