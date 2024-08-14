package example.neoed.common.item

import example.neoed.Neoed
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object NeoedCreativeModeTabs {
    val REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Neoed.ID)

    val NEOED: CreativeModeTab by REGISTRY.register("neoed") { ->
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.literal("Neoed"))
            .icon { -> InfinityBucketItem.defaultInstance }
            .displayItems { parameters, output ->
                NeoedItems.REGISTRY.entries.forEach { output.accept(it.get()) }
            }
            .build()
    }
}
