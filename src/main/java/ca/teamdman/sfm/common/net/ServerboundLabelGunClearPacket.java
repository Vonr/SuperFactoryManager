package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundLabelGunClearPacket(
        InteractionHand hand
) {
    public static void encode(ServerboundLabelGunClearPacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunClearPacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunClearPacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunClearPacket msg, Supplier<NetworkEvent.Context> ctx
    ) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            if (stack.getItem() instanceof LabelGunItem) {
                LabelHolder.empty().save(stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
