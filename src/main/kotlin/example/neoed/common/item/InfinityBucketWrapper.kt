package example.neoed.common.item

import example.neoed.common.component.getFluidContents
import example.neoed.common.component.getFluidStacks
import example.neoed.common.component.setFluidStacks
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidStack.isSameFluidSameComponents
import net.neoforged.neoforge.fluids.SimpleFluidContent
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class InfinityBucketWrapper(private val container: ItemStack) : IFluidHandlerItem {
    override fun getContainer(): ItemStack = container

    override fun getTanks(): Int = container.getFluidContents().size.coerceAtLeast(1)

    override fun getFluidInTank(tank: Int): FluidStack = container.getFluidStacks().getOrNull(tank) ?: FluidStack.EMPTY

    override fun getTankCapacity(tank: Int): Int = Integer.MAX_VALUE

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = true

    override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (container.count != 1 && resource.isEmpty) {
            return 0
        }

        val fluids = container.getFluidStacks()
        val contained: FluidStack = fluids
            .firstOrNull { isSameFluidSameComponents(resource, it) }
            ?: resource.copyWithAmount(0)
        val fillAmount: Int = (Integer.MAX_VALUE - contained.amount).coerceAtMost(resource.amount)

        if (action.execute()) {
            contained.grow(fillAmount)
            fluids.remove(contained)
            fluids.addFirst(contained)
            container.setFluidStacks(fluids)
        }

        return fillAmount
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        if (container.count != 1 || resource.isEmpty) {
            return FluidStack.EMPTY
        }
        val contained = container.getFluidContents().firstOrNull() ?: SimpleFluidContent.EMPTY
        if (!contained.isSameFluidSameComponents(resource)) {
            return FluidStack.EMPTY
        }
        return drain(resource.amount, action)
    }

    override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
        if (container.count != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY
        }

        val fluids = container.getFluidStacks()
        val contained: FluidStack = fluids.firstOrNull() ?: FluidStack.EMPTY
        val drainAmount: Int = maxDrain.coerceAtMost(contained.amount)
        val drained: FluidStack = contained.copyWithAmount(drainAmount)

        if (action.execute()) {
            contained.shrink(drainAmount)
            fluids.removeIf { it.isEmpty }
            container.setFluidStacks(fluids)
        }

        return drained
    }
}
