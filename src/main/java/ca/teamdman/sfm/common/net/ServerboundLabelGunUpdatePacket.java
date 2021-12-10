package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.item.LabelGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundLabelGunUpdatePacket {
    private final String          LABEL;
    private final InteractionHand HAND;

    public ServerboundLabelGunUpdatePacket(String label, InteractionHand hand) {
        LABEL = label;
        HAND  = hand;
    }

    public static void encode(ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.LABEL, 80);
        buf.writeEnum(msg.HAND);
    }

    public static ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunUpdatePacket(buf.readUtf(80), buf.readEnum(InteractionHand.class));
    }

    public static void handle(
            ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket msg,
            Supplier<NetworkEvent.Context> ctx
    ) {
        ctx
                .get()
                .enqueueWork(() -> {
                    var sender = ctx
                            .get()
                            .getSender();
                    var stack = sender.getItemInHand(msg.HAND);
                    LabelGunItem.setLabel(stack, msg.LABEL);
                });
        ctx
                .get()
                .setPacketHandled(true);
    }
}
