package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public record ServerboundManagerSetLogLevelPacket(
        int windowId,
        BlockPos pos,
        String logLevel
) {
    public static final int MAX_LOG_LEVEL_NAME_LENGTH = 64;

    public static void encode(ServerboundManagerSetLogLevelPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
        friendlyByteBuf.writeUtf(msg.logLevel(), MAX_LOG_LEVEL_NAME_LENGTH);
    }

    public static ServerboundManagerSetLogLevelPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerSetLogLevelPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readUtf(MAX_LOG_LEVEL_NAME_LENGTH)
        );
    }

    public static void handle(ServerboundManagerSetLogLevelPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        SFMPackets.handleServerboundContainerPacket(
                contextSupplier,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    // get the level
                    Level logLevelObj = Level.getLevel(msg.logLevel());

                    // set the level
                    manager.setLogLevel(logLevelObj);

                    // log in manager
                    manager.logger.info(x -> x.accept(Constants.LocalizationKeys.LOG_LEVEL_UPDATED.get(
                            msg.logLevel())));

                    // log in server console
                    String sender = "UNKNOWN SENDER";
                    ServerPlayer player = contextSupplier.get().getSender();
                    if (player != null) {
                        sender = player.getName().getString();
                    }
                    SFM.LOGGER.debug("{} updated manager {} {} log level to {}", sender, msg.pos(), manager.getLevel(), msg.logLevel());
                }
        );
    }
}
