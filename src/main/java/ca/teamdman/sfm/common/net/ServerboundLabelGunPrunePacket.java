package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundLabelGunPrunePacket(
        InteractionHand hand
) {
    public static void encode(ServerboundLabelGunPrunePacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
    }

    public static ServerboundLabelGunPrunePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunPrunePacket(buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ServerboundLabelGunPrunePacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            var sender = contextSupplier.get().getSender();
            if (sender == null) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            if (stack.getItem() instanceof LabelGunItem) {
                LabelPositionHolder.from(stack).prune().save(stack);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
