package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.LabelHolder;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public record ServerboundLabelInspectionRequestPacket(
        String label
) {
    public static void encode(ServerboundLabelInspectionRequestPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(msg.label(), Program.MAX_LABEL_LENGTH);
    }

    public static ServerboundLabelInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundLabelInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_LABEL_LENGTH)
        );
    }

    public static void handle(
            ServerboundLabelInspectionRequestPacket msg,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            // we don't know if the player has the program edit screen open from a manager or a disk in hand
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;
            LabelHolder labelHolder;
            if (player.containerMenu instanceof ManagerContainerMenu mcm) {
                System.out.println("manager menu!");
                labelHolder = LabelHolder.from(mcm.CONTAINER.getItem(0));
            } else {
                if (player.getMainHandItem().is(SFMItems.DISK_ITEM.get())) {
                    labelHolder = LabelHolder.from(player.getMainHandItem());
                } else if (player.getOffhandItem().is(SFMItems.DISK_ITEM.get())) {
                    labelHolder = LabelHolder.from(player.getOffhandItem());
                } else {
                    labelHolder = null;
                }
            }
            if (labelHolder == null) return;

            StringBuilder payload = new StringBuilder();
            payload.append("-- Positions for label \"").append(msg.label()).append("\" --\n");

            labelHolder.get().getOrDefault(msg.label(), List.of()).forEach(pos -> {
                payload
                        .append(pos.getX())
                        .append(",")
                        .append(pos.getY())
                        .append(",")
                        .append(pos.getZ());
                if (player.getLevel().isLoaded(pos)) {
                    payload
                            .append(" -- ")
                            .append(player.getLevel().getBlockState(pos).getBlock().getName().getString());
                } else {
                    payload
                            .append(" -- chunk not loaded");
                }
                payload
                        .append("\n");
            });
            SFMPackets.INSPECTION_CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new ClientboundLabelInspectionResultsPacket(
                            payload.toString()
                    )
            );
        });
    }
}
