package example.neoed

import example.neoed.client.model.InfinityBucketItemModel
import example.neoed.common.component.NeoedDataComponents
import example.neoed.common.item.InfinityBucketItem
import example.neoed.common.item.InfinityBucketWrapper
import example.neoed.common.item.NeoedCreativeModeTabs
import example.neoed.common.item.NeoedItems
import example.neoed.common.network.CrouchScrollPayload
import example.neoed.common.network.HoverScrollPayload
import example.neoed.server.network.CrouchScrollHandler
import example.neoed.server.network.HoverScrollHandler
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(Neoed.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object Neoed {
    const val ID = "neoed"

    init {
        NeoedItems.REGISTRY.register(MOD_BUS)
        NeoedDataComponents.REGISTRY.register(MOD_BUS)
        NeoedCreativeModeTabs.REGISTRY.register(MOD_BUS)
    }

    /*
     * Register custom item model for the infinity bucket.
     */
    @SubscribeEvent
    fun onRegisterGeometryLoaders(event: RegisterGeometryLoaders) {
        event.register(
            ResourceLocation.fromNamespaceAndPath(ID, "infinity_bucket_loader"),
            InfinityBucketItemModel.Loader
        )
    }

    /*
     * Register fluid capability for the infinity bucket.
     */
    @SubscribeEvent
    fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerItem(
            Capabilities.FluidHandler.ITEM,
            { stack, _ -> InfinityBucketWrapper(stack) },
            InfinityBucketItem
        )
    }

    /*
     * Register event handler for mouse scrolling on the infinite bucket.
     */
    @SubscribeEvent
    fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToServer(
            HoverScrollPayload.TYPE,
            HoverScrollPayload.STREAM_CODEC,
            HoverScrollHandler
        )
        registrar.playToServer(
            CrouchScrollPayload.TYPE,
            CrouchScrollPayload.STREAM_CODEC,
            CrouchScrollHandler
        )
    }
}
