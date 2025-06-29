package example.infinity_bucket.common.item

import example.infinity_bucket.InfinityBucketMod
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModItems {
    val REGISTRY = DeferredRegister.createItems(InfinityBucketMod.ID)

    val INFINITY_BUCKET_ITEM by REGISTRY.register("infinity_bucket") { -> InfinityBucketItem }
}
