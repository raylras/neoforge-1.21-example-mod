package example.neoed.client

import example.neoed.common.component.getFluidContents
import example.neoed.common.item.InfinityBucketItem
import example.neoed.common.network.CrouchScrollPayload
import example.neoed.common.network.HoverScrollPayload
import example.neoed.common.network.Scroll
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber(value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.GAME)
object ClientEventHandler {
    @SubscribeEvent
    fun onMouseScroll(event: MouseScrollingEvent) {
        val player = Minecraft.getInstance().player ?: return
        if (!player.isCrouching) return

        val mainHandItem = player.mainHandItem
        if (mainHandItem.item != InfinityBucketItem) return

        event.isCanceled = true

        val fluids = mainHandItem.getFluidContents()
        if (fluids.size <= 1) return

        if (event.scrollDeltaY < 0) { // scroll down
            PacketDistributor.sendToServer(CrouchScrollPayload(Scroll.DOWN))
        } else if (event.scrollDeltaY > 0) { // scroll up
            PacketDistributor.sendToServer(CrouchScrollPayload(Scroll.UP))
        }
    }

    @SubscribeEvent
    fun onScreenMouseScroll(event: ScreenEvent.MouseScrolled.Pre) {
        val screen = event.screen as? AbstractContainerScreen<*> ?: return
        val hoveredSlot = screen.slotUnderMouse ?: return
        val hoveredItem = hoveredSlot.item ?: return
        if (hoveredItem.item != InfinityBucketItem) return

        event.isCanceled = true

        val fluids = hoveredItem.getFluidContents()
        if (fluids.size <= 1) return

        if (event.scrollDeltaY < 0) { // scroll down
            PacketDistributor.sendToServer(HoverScrollPayload(hoveredSlot.index, Scroll.DOWN))
        } else if (event.scrollDeltaY > 0) { // scroll up
            PacketDistributor.sendToServer(HoverScrollPayload(hoveredSlot.index, Scroll.UP))
        }
    }
}
