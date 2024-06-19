package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

public record ServerboundManagerRebuildPacket(
        int windowId,
        BlockPos pos
) {
    public static void encode(ServerboundManagerRebuildPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundManagerRebuildPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerRebuildPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(ServerboundManagerRebuildPacket msg, NetworkEvent.Context ctx) {
        SFMPackets.handleServerboundContainerPacket(
                ctx,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    // perform rebuild by unregistering the cable network
                    CableNetworkManager.purgeCableNetworkForManager(manager);
                    manager.logger.warn(x -> x.accept(Constants.LocalizationKeys.LOG_MANAGER_CABLE_NETWORK_REBUILD.get()));

                    // log it
                    String sender = "UNKNOWN SENDER";
                    ServerPlayer player = ctx.getSender();
                    if (player != null) {
                        sender = player.getName().getString();
                    }
                    SFM.LOGGER.debug("{} performed rebuild for manager {} {}", sender, msg.pos(), manager.getLevel());
                }
        );
        contextSupplier.get().setPacketHandled(true);
    }
}
