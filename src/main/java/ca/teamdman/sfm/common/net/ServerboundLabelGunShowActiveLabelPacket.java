package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundLabelGunShowActiveLabelPacket(
        InteractionHand hand
) implements CustomPacketPayload {

    public static final Type<ServerboundLabelGunShowActiveLabelPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_label_gun_toggle_show_active_label_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundLabelGunShowActiveLabelPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundLabelGunShowActiveLabelPacket::encode,
            ServerboundLabelGunShowActiveLabelPacket::decode
    );

    public static void encode(
            ServerboundLabelGunShowActiveLabelPacket msg,
            FriendlyByteBuf buf
    ) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunShowActiveLabelPacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunShowActiveLabelPacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunShowActiveLabelPacket msg,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer sender)) {
            return;
        }
        ItemStack gun = sender.getItemInHand(msg.hand);
        if (gun.getItem() instanceof LabelGunItem) {
            boolean active = LabelGunItem.getOnlyShowActiveLabel(gun);
            gun.set(SFMDataComponents.ONLY_SHOW_ACTIVE_LABEL, !active);
        }

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

