package example.neoed.common.network

import com.mojang.serialization.Codec
import example.neoed.Neoed
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs

class InfinityBucketScrollingPayload(val scroll: Scroll) : CustomPacketPayload {
    val scrollUp: Boolean = scroll == Scroll.UP
    val scrollDown: Boolean = scroll == Scroll.DOWN

    override fun type(): CustomPacketPayload.Type<InfinityBucketScrollingPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<InfinityBucketScrollingPayload> = CustomPacketPayload.Type(
            ResourceLocation.fromNamespaceAndPath(
                Neoed.ID,
                "infinity_bucket_scrolling"
            )
        )

        val CODEC: Codec<InfinityBucketScrollingPayload> =
            Scroll.CODEC.xmap(::InfinityBucketScrollingPayload, InfinityBucketScrollingPayload::scroll)

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, InfinityBucketScrollingPayload> =
            Scroll.STREAM_CODEC.map(::InfinityBucketScrollingPayload, InfinityBucketScrollingPayload::scroll)
    }

    enum class Scroll : StringRepresentable {
        UP, DOWN;

        override fun getSerializedName(): String {
            return this.name.lowercase()
        }

        companion object {
            val CODEC: Codec<Scroll> = StringRepresentable.fromEnum(Scroll::values)

            val STREAM_CODEC: StreamCodec<FriendlyByteBuf, Scroll> =
                NeoForgeStreamCodecs.enumCodec(Scroll::class.java)
        }
    }
}
