package example.infinity_bucket.server.network

import example.infinity_bucket.common.fluid.getFluidContents
import example.infinity_bucket.common.fluid.setFluidContents
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

    val contents = bucket.getFluidContents().toMutableList()
    if (contents.size <= 1) return

    if (scroll.down) {
        val first = contents.removeFirst()
        contents.addLast(first)
        bucket.setFluidContents(contents)
    } else if (scroll.up) {
        val last = contents.removeLast()
        contents.addFirst(last)
        bucket.setFluidContents(contents)
    }
}
