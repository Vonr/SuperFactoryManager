package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundLabelGunPrunePacket(
        InteractionHand hand
) implements CustomPacketPayload {

    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_label_gun_prune_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundLabelGunPrunePacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundLabelGunPrunePacket::encode,
            ServerboundLabelGunPrunePacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundLabelGunPrunePacket msg,
            FriendlyByteBuf buf
    ) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunPrunePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunPrunePacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunPrunePacket msg,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer sender)) {
            return;
        }
        var stack = sender.getItemInHand(msg.hand);
        if (stack.getItem() instanceof LabelGunItem) {
            LabelPositionHolder.from(stack).prune().save(stack);
        }

    }
}
