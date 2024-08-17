package example.neoed.common.item

import example.neoed.common.component.getFluidStacks
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidUtil
import java.text.DecimalFormat

object InfinityBucketItem : Item(
    Properties()
        .stacksTo(1)
        .rarity(Rarity.EPIC)
        .fireResistant()
) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val fluids: List<FluidStack> = stack.getFluidStacks()
        for (fluid in fluids) {
            tooltipComponents.add(fluid.toMessageComponent())
        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (entity is Player && entity.getInventory().getSelected() == stack) {
            FluidUtil.getFluidContained(stack).ifPresent { fluid ->
                entity.displayClientMessage(fluid.toMessageComponent(), true)
            }
        }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val usedItem = player.getItemInHand(usedHand)
        if (usedItem.count != 1) {
            return InteractionResultHolder.fail(usedItem)
        }

        val hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY)
        if (hitResult.type != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(usedItem)
        }

        val hitPos = hitResult.blockPos
        if (player.mayInteract(level, hitPos)) {
            val pickUpResult = FluidUtil.tryPickUpFluid(usedItem, player, level, hitPos, hitResult.direction)
            if (pickUpResult.isSuccess) {
                return InteractionResultHolder.success(pickUpResult.getResult())
            }
        }

        val placePos = hitPos.offset(hitResult.direction.normal)
        if (player.mayInteract(level, placePos)) {
            val fluid = FluidUtil.getFluidContained(usedItem).orElse(FluidStack.EMPTY)
            val placeResult = FluidUtil.tryPlaceFluid(player, level, usedHand, placePos, usedItem, fluid)
            if (placeResult.isSuccess) {
                return InteractionResultHolder.success(placeResult.getResult())
            }
        }

        return InteractionResultHolder.pass(usedItem)
    }

    private fun FluidStack.toMessageComponent(): Component {
        val displayName = this.hoverName
        val amount = FMT.format(this.amount)
        return displayName.copy().apply {
            append(" ")
            append(amount)
            append(" ")
            append("mL")
        }
    }
}

private val FMT = DecimalFormat(",###")
