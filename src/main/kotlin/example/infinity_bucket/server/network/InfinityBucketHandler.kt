package example.infinity_bucket.server.network

import example.infinity_bucket.common.component.getFluidStacks
import example.infinity_bucket.common.component.setFluidStacks
import example.infinity_bucket.common.item.InfinityBucketItem
import example.infinity_bucket.common.network.CrouchScrollPayload
import example.infinity_bucket.common.network.HoverScrollPayload
import example.infinity_bucket.common.network.Scroll
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler

object CrouchScrollHandler: IPayloadHandler<CrouchScrollPayload> {
    override fun handle(payload: CrouchScrollPayload, context: IPayloadContext) {
        handleScroll(payload.scroll, context.player().mainHandItem)
    }
}

object HoverScrollHandler: IPayloadHandler<HoverScrollPayload> {
    override fun handle(payload: HoverScrollPayload, context: IPayloadContext) {
        val slot = context.player().containerMenu.getSlot(payload.slotId)
        handleScroll(payload.scroll, slot.item)
    }
}

private fun handleScroll(scroll: Scroll, bucket: ItemStack) {
    if (bucket.item != InfinityBucketItem) return

    val fluids = bucket.getFluidStacks()
    if (fluids.size <= 1) return

    if (scroll.down) {
        val first = fluids.removeFirst()
        fluids.addLast(first)
        bucket.setFluidStacks(fluids)
    } else if (scroll.up) {
        val last = fluids.removeLast()
        fluids.addFirst(last)
        bucket.setFluidStacks(fluids)
    }
}
