package example.infinity_bucket.common.item

import example.infinity_bucket.common.fluid.FluidContent
import example.infinity_bucket.common.fluid.getFluidContents
import example.infinity_bucket.common.fluid.setFluidContents
import example.infinity_bucket.common.fluid.toFluidContentWithAmount
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class InfinityBucketWrapper(private val container: ItemStack) : IFluidHandlerItem {
    override fun getContainer(): ItemStack = container

    override fun getTanks(): Int = container.getFluidContents().size.coerceAtLeast(1)

    override fun getFluidInTank(tank: Int): FluidStack {
        return container.getFluidContents().getOrElse(tank) { FluidContent.EMPTY }.toFluidStack()
    }

    override fun getTankCapacity(tank: Int): Int = Integer.MAX_VALUE

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = true

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (container.count != 1 && resource.isEmpty) {
            return 0
        }

        val contents = container.getFluidContents()
        val content = contents.firstOrNull { it.isSameFluidSameComponents(resource) } ?: resource.toFluidContentWithAmount(0)

        val availableAmount = Long.MAX_VALUE - content.amount
        val fillAmount = resource.amount.toLong()
            .coerceAtMost(availableAmount)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()

        if (action.execute()) {
            val newContents = contents.toMutableList()
            val newContent = content.copyWithAmount(content.amount + fillAmount)
            newContents.remove(content)
            newContents.addFirst(newContent)
            container.setFluidContents(newContents)
        }

        return fillAmount
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if (container.count != 1 || resource.isEmpty) {
            return FluidStack.EMPTY
        }

        val content = container.getFluidContents().getOrElse(0) { FluidContent.EMPTY }
        if (!content.isSameFluidSameComponents(resource)) {
            return FluidStack.EMPTY
        }

        return drain(resource.amount, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if (container.count != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY
        }

        val contents = container.getFluidContents()
        if (contents.isEmpty()) {
            return FluidStack.EMPTY
        }

        val content = contents.first()
        val drainAmount = content.amount.coerceAtMost(maxDrain.toLong())
        val stack = FluidStack(content.fluid, drainAmount.toInt())

        if (action.execute()) {
            val newContents = contents.toMutableList()
            val newContent = content.copyWithAmount(content.amount - drainAmount)
            newContents[0] = newContent
            container.setFluidContents(newContents)
        }

        return stack
    }
}
