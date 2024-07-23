package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundLabelGunUpdatePacket(
        String label,
        InteractionHand hand
) implements CustomPacketPayload {

    public static final Type<ServerboundLabelGunUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_label_gun_update_packet"
    ));
    public static final int MAX_LABEL_LENGTH = 80;
    public static final StreamCodec<FriendlyByteBuf, ServerboundLabelGunUpdatePacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundLabelGunUpdatePacket::encode,
            ServerboundLabelGunUpdatePacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundLabelGunUpdatePacket msg,
            FriendlyByteBuf buf
    ) {
        buf.writeUtf(msg.label, MAX_LABEL_LENGTH);
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunUpdatePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunUpdatePacket(buf.readUtf(MAX_LABEL_LENGTH), buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunUpdatePacket msg,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer sender)) {
            return;
        }
        var stack = sender.getItemInHand(msg.hand);
        if (stack.getItem() instanceof LabelGunItem) {
            LabelGunItem.setActiveLabel(stack, msg.label);
        }

    }
}

