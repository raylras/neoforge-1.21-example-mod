package example.neoed.client

import example.neoed.common.component.getFluidStacks
import example.neoed.common.item.InfinityBucketItem
import example.neoed.common.network.InfinityBucketScrollingPayload
import example.neoed.common.network.InfinityBucketScrollingPayload.Scroll
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber(value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.GAME)
object ClientEventHandler {
    @SubscribeEvent
    fun onMouseScrolling(event: MouseScrollingEvent) {
        val player = Minecraft.getInstance().player
        if (player == null) return
        if (!player.isCrouching) return

        val mainHandItem = player.mainHandItem
        if (mainHandItem == null || mainHandItem.item != InfinityBucketItem) return
        event.isCanceled = true

        val fluids = mainHandItem.getFluidStacks()
        if (fluids.size <= 1) return

        if (event.scrollDeltaY < 0) { // scroll down
            PacketDistributor.sendToServer(InfinityBucketScrollingPayload(Scroll.DOWN))
        } else if (event.scrollDeltaY > 0) { // scroll up
            PacketDistributor.sendToServer(InfinityBucketScrollingPayload(Scroll.UP))
        }
    }
}
