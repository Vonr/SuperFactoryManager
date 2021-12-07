package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.MenuPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class SFMPackets {
    public static final String        MANAGER_CHANNEL_VERSION = "1";
    public static final SimpleChannel MANAGER_CHANNEL         = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SFM.MOD_ID, "manager"),
            MANAGER_CHANNEL_VERSION::toString,
            MANAGER_CHANNEL_VERSION::equals,
            MANAGER_CHANNEL_VERSION::equals
    );

    public static <MSG extends MenuPacket> void register(
            SimpleChannel channel,
            int id,
            Class<MSG> clazz,
            MenuPacket.MenuPacketHandler<?, ?, MSG> handler
    ) {
        channel.registerMessage(id, clazz, handler::_encode, handler::_decode, handler::_handle);
    }

    public static void register() {
        register(
                MANAGER_CHANNEL,
                0,
                ServerboundManagerProgramPacket.class,
                new ServerboundManagerProgramPacket.ResetPacketHandler()
        );
    }
}
