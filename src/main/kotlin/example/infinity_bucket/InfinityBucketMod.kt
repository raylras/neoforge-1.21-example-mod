package example.infinity_bucket

import example.infinity_bucket.client.model.InfinityBucketItemModel
import example.infinity_bucket.common.component.ModDataComponents
import example.infinity_bucket.common.item.InfinityBucketItem
import example.infinity_bucket.common.item.InfinityBucketWrapper
import example.infinity_bucket.common.item.ModCreativeModeTabs
import example.infinity_bucket.common.item.ModItems
import example.infinity_bucket.common.network.CrouchScrollPayload
import example.infinity_bucket.common.network.HoverScrollPayload
import example.infinity_bucket.server.network.CrouchScrollHandler
import example.infinity_bucket.server.network.HoverScrollHandler
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(InfinityBucketMod.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object InfinityBucketMod {
    const val ID = "infinity_bucket_mod"

    init {
        ModItems.REGISTRY.register(MOD_BUS)
        ModDataComponents.REGISTRY.register(MOD_BUS)
        ModCreativeModeTabs.REGISTRY.register(MOD_BUS)
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
