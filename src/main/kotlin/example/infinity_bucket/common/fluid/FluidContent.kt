package example.infinity_bucket.common.fluid

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import example.infinity_bucket.common.component.ModDataComponents
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack

data class FluidContent(val fluid: Fluid, val amount: Long, val components: DataComponentPatch) {

    fun isEmpty(): Boolean {
        return (fluid == Fluids.EMPTY) || (amount <= 0L)
    }

    fun isSameFluidSameComponents(stack: FluidStack): Boolean {
        return (fluid == stack.fluid) && (components == stack.componentsPatch)
    }

    fun copyWithAmount(amount: Long): FluidContent {
        return FluidContent(fluid, amount, components)
    }

    fun toFluidStack(): FluidStack {
        return FluidStack(fluid.builtInRegistryHolder(), amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), components)
    }

    companion object {
        val EMPTY = FluidContent(Fluids.EMPTY, 0L, DataComponentPatch.EMPTY)

        val CODEC: Codec<FluidContent> = RecordCodecBuilder.create { instance ->
            instance
                .group(
                    FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter { it.fluid.builtInRegistryHolder() },
                    Codec.LONG.fieldOf("amount").forGetter { it.amount },
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                        .forGetter { it.components }
                )
                .apply(instance) { holder, amount, components -> FluidContent(holder.value(), amount, components) }
        }

        val STREAM_CODEC = object : StreamCodec<RegistryFriendlyByteBuf, FluidContent> {
            private val FLUID_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Holder<Fluid>> =
                ByteBufCodecs.holderRegistry(Registries.FLUID)

            override fun decode(buf: RegistryFriendlyByteBuf): FluidContent {
                val amount = buf.readVarLong()
                val holder = FLUID_STREAM_CODEC.decode(buf)
                val patch = DataComponentPatch.STREAM_CODEC.decode(buf)
                return FluidContent(holder.value(), amount, patch)
            }

            override fun encode(buf: RegistryFriendlyByteBuf, content: FluidContent) {
                buf.writeVarLong(content.amount)
                FLUID_STREAM_CODEC.encode(buf, content.fluid.builtInRegistryHolder())
                DataComponentPatch.STREAM_CODEC.encode(buf, content.components)
            }
        }
    }
}

fun ItemStack.getFluidContents(): List<FluidContent> {
    return this.getOrDefault(ModDataComponents.FLUIDS, emptyList())
}

fun ItemStack.setFluidContents(fluids: List<FluidContent>) {
    val fluids = fluids.filter { !it.isEmpty() }
    if (fluids.isEmpty()) {
        this.remove(ModDataComponents.FLUIDS)
    } else {
        this.set(ModDataComponents.FLUIDS, fluids)
    }
}

fun FluidStack.toFluidContentWithAmount(amount: Long): FluidContent {
    return FluidContent(fluid, amount, componentsPatch)
}
