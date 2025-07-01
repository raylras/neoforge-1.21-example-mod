package example.infinity_bucket.common.component

import example.infinity_bucket.InfinityBucketMod
import example.infinity_bucket.common.fluid.FluidContent
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModDataComponents {
    val REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, InfinityBucketMod.ID)

    val FLUIDS: DataComponentType<List<FluidContent>> by REGISTRY.register("fluids") { ->
        DataComponentType.builder<List<FluidContent>>()
            .persistent(FluidContent.CODEC.listOf())
            .networkSynchronized(FluidContent.STREAM_CODEC.apply(ByteBufCodecs.list()))
            .build()
    }
}
