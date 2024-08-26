package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public static void handle(ServerboundManagerRebuildPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        SFMPackets.handleServerboundContainerPacket(
                contextSupplier,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    // perform rebuild by unregistering the cable network
                    CableNetworkManager.purgeCableNetworkForManager(manager);
                    manager.logger.warn(x -> x.accept(LocalizationKeys.LOG_MANAGER_CABLE_NETWORK_REBUILD.get()));

                    // log it
                    String sender = "UNKNOWN SENDER";
                    ServerPlayer player = contextSupplier.get().getSender();
                    if (player != null) {
                        sender = player.getName().getString();
                    }
                    SFM.LOGGER.debug("{} performed rebuild for manager {} {}", sender, msg.pos(), manager.getLevel());
                }
        );
        contextSupplier.get().setPacketHandled(true);
    }
}
