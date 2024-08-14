package example.neoed.common.item

import example.neoed.Neoed
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object NeoedItems {
    val REGISTRY = DeferredRegister.createItems(Neoed.ID)

    val INFINITY_BUCKET_ITEM by REGISTRY.register("infinity_bucket") { -> InfinityBucketItem }
}
