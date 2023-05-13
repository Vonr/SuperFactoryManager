package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.item.LabelGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundLabelGunUpdatePacket(
        String label,
        InteractionHand hand
) {
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
            ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket msg, Supplier<NetworkEvent.Context> ctx
    ) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            if (stack.getItem() instanceof LabelGunItem) {
                LabelGunItem.setLabel(stack, msg.label);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
