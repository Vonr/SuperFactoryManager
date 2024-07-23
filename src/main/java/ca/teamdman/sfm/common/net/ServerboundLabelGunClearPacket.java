package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundLabelGunClearPacket(
        InteractionHand hand
) implements CustomPacketPayload {

    public static final Type<ServerboundLabelGunClearPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_label_gun_clear_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundLabelGunClearPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundLabelGunClearPacket::encode,
            ServerboundLabelGunClearPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundLabelGunClearPacket msg,
            FriendlyByteBuf buf
    ) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunClearPacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunClearPacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunClearPacket msg,
            IPayloadContext context
    ) {
        var sender = context.player();

        var stack = sender.getItemInHand(msg.hand);
        if (stack.getItem() instanceof LabelGunItem) {
            LabelPositionHolder.empty().save(stack);
        }

    }
}

