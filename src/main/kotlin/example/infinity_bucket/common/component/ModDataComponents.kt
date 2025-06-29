package example.infinity_bucket.common.component

import example.infinity_bucket.InfinityBucketMod
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.SimpleFluidContent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModDataComponents {
    val REGISTRY = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, InfinityBucketMod.ID)

    val FLUIDS: DataComponentType<List<SimpleFluidContent>> by REGISTRY.register("fluids") { ->
        DataComponentType.builder<List<SimpleFluidContent>>()
            .persistent(SimpleFluidContent.CODEC.listOf())
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC.apply(ByteBufCodecs.list()))
            .build()
    }
}

/**
 * @return Mutable list of **modifiable** fluids
 */
fun ItemStack.getFluidStacks(): MutableList<FluidStack> {
    return this.getFluidContents().map(SimpleFluidContent::copy).toMutableList()
}

fun ItemStack.setFluidStacks(fluids: List<FluidStack>) {
    this.setFluidContents(fluids.map(SimpleFluidContent::copyOf))
}

/**
 *
 * @return Immutable list of **unmodifiable** fluids, see [Data Components](https://docs.neoforged.net/docs/items/datacomponents/#creating-custom-data-components).
 */
fun ItemStack.getFluidContents(): List<SimpleFluidContent> {
    return this.getOrDefault(ModDataComponents.FLUIDS, emptyList())
}

fun ItemStack.setFluidContents(fluids: List<SimpleFluidContent>) {
    if (fluids.isEmpty()) {
        this.remove(ModDataComponents.FLUIDS)
    } else {
        this.set(ModDataComponents.FLUIDS, fluids)
    }
}
