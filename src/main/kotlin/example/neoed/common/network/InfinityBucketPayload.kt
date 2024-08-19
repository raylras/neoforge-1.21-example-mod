package example.neoed.common.network

import example.neoed.Neoed
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs

data class HoverScrollPayload(val slotId: Int, val scroll: Scroll) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<HoverScrollPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<HoverScrollPayload> = CustomPacketPayload.Type(
            ResourceLocation.fromNamespaceAndPath(
                Neoed.ID,
                "infinity_bucket_hover_scrolling"
            )
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, HoverScrollPayload> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, HoverScrollPayload::slotId,
            Scroll.STREAM_CODEC, HoverScrollPayload::scroll,
            ::HoverScrollPayload
        )
    }
}

data class CrouchScrollPayload(val scroll: Scroll) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<CrouchScrollPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<CrouchScrollPayload> = CustomPacketPayload.Type(
            ResourceLocation.fromNamespaceAndPath(
                Neoed.ID,
                "infinity_bucket_crouch_scrolling"
            )
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, CrouchScrollPayload> =
            Scroll.STREAM_CODEC.map(::CrouchScrollPayload, CrouchScrollPayload::scroll)
    }
}

enum class Scroll : StringRepresentable {
    UP, DOWN;

    val up: Boolean
        get() = this == UP

    val down: Boolean
        get() = this == DOWN

    override fun getSerializedName(): String {
        return this.name.lowercase()
    }

    companion object {
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, Scroll> =
            NeoForgeStreamCodecs.enumCodec(Scroll::class.java)
    }
}
