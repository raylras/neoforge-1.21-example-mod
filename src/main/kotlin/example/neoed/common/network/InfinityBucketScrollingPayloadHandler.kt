package example.neoed.common.network

import example.neoed.common.component.getFluidStacks
import example.neoed.common.component.setFluidStacks
import example.neoed.common.item.InfinityBucketItem
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler

class InfinityBucketScrollingPayloadHandler: IPayloadHandler<InfinityBucketScrollingPayload> {
    override fun handle(payload: InfinityBucketScrollingPayload, context: IPayloadContext) {
        val player = context.player()
        val mainHandItem = player.mainHandItem
        if (mainHandItem == null || mainHandItem.item != InfinityBucketItem) return

        val fluids = mainHandItem.getFluidStacks()
        if (fluids.size <= 1) return

        if (payload.scrollDown) {
            val first = fluids.removeFirst()
            fluids.addLast(first)
            mainHandItem.setFluidStacks(fluids)
        } else if (payload.scrollUp) {
            val last = fluids.removeLast()
            fluids.addFirst(last)
            mainHandItem.setFluidStacks(fluids)
        }
    }
}
