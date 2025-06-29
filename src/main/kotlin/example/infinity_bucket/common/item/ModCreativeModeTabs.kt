package example.infinity_bucket.common.item

import example.infinity_bucket.InfinityBucketMod
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModCreativeModeTabs {
    val REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InfinityBucketMod.ID)

    val INFINITY_BUCKET_MOD: CreativeModeTab by REGISTRY.register("infinity_bucket_mod") { ->
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.literal("Infinity Bucket"))
            .icon { -> InfinityBucketItem.defaultInstance }
            .displayItems { parameters, output ->
                ModItems.REGISTRY.entries.forEach { output.accept(it.get()) }
            }
            .build()
    }
}
