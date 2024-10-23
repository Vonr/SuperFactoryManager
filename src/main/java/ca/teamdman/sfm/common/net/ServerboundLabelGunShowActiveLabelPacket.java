package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.item.LabelGunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundLabelGunShowActiveLabelPacket(
        InteractionHand hand
) {

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
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender == null) return;
            ItemStack gun = sender.getItemInHand(msg.hand);
            if (gun.getItem() instanceof LabelGunItem) {
                boolean active = LabelGunItem.getOnlyShowActiveLabel(gun);
                LabelGunItem.setOnlyShowActiveLabel(gun, !active);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}

