package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;


import java.util.function.Supplier;

public record ServerboundLabelGunPrunePacket(
        InteractionHand hand
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "serverbound_label_gun_prune_packet");
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(SFM.MOD_ID, getClass().getSimpleName());
    }
    public static void encode(ServerboundLabelGunPrunePacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunPrunePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunPrunePacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunPrunePacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(() -> {
            if (!(context.player().orElse(null) instanceof ServerPlayer sender)) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            if (stack.getItem() instanceof LabelGunItem) {
                LabelPositionHolder.from(stack).prune().save(stack);
            }
        });
        
    }
}
