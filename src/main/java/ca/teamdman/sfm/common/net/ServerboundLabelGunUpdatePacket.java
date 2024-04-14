package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;


import java.util.function.Supplier;

public record ServerboundLabelGunUpdatePacket(
        String label,
        InteractionHand hand
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "serverbound_label_gun_update_packet");
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(SFM.MOD_ID, getClass().getSimpleName());
    }
    public static final int MAX_LABEL_LENGTH = 80;

    public static void encode(ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.label, MAX_LABEL_LENGTH);
        buf.writeEnum(msg.hand);
    }

    public static ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunUpdatePacket(buf.readUtf(MAX_LABEL_LENGTH), buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(() -> {
            if (!(context.player().orElse(null) instanceof ServerPlayer sender)) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            if (stack.getItem() instanceof LabelGunItem) {
                LabelGunItem.setActiveLabel(stack, msg.label);
            }
        });
        
    }
}
