package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;


import java.util.function.Supplier;

public record ServerboundLabelGunClearPacket(
        InteractionHand hand
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "serverbound_label_gun_clear_packet");
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(SFM.MOD_ID, getClass().getSimpleName());
    }
    public static void encode(ServerboundLabelGunClearPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunClearPacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunClearPacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunClearPacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(() -> {
            var sender = context.player();
            if (sender.isEmpty()) {
                return;
            }
            
            var stack = sender.get().getItemInHand(msg.hand);
            if (stack.getItem() instanceof LabelGunItem) {
                LabelPositionHolder.empty().save(stack);
            }
        });
        
    }
}
