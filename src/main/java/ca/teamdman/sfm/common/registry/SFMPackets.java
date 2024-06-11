package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SFMPackets {
    public static final String MANAGER_CHANNEL_VERSION = "1";
    public static final String LABEL_GUN_ITEM_CHANNEL_VERSION = "1";
    public static final String DISK_ITEM_CHANNEL_VERSION = "1";
    public static final String INSPECTION_CHANNEL_VERSION = "1";
    public static final SimpleChannel MANAGER_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SFM.MOD_ID, "manager"),
            MANAGER_CHANNEL_VERSION::toString,
            MANAGER_CHANNEL_VERSION::equals,
            MANAGER_CHANNEL_VERSION::equals
    );
    public static final SimpleChannel LABEL_GUN_ITEM_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SFM.MOD_ID, "labelgun"),
            LABEL_GUN_ITEM_CHANNEL_VERSION::toString,
            LABEL_GUN_ITEM_CHANNEL_VERSION::equals,
            LABEL_GUN_ITEM_CHANNEL_VERSION::equals
    );
    public static final SimpleChannel DISK_ITEM_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SFM.MOD_ID, "disk"),
            DISK_ITEM_CHANNEL_VERSION::toString,
            DISK_ITEM_CHANNEL_VERSION::equals,
            DISK_ITEM_CHANNEL_VERSION::equals
    );

    public static final SimpleChannel INSPECTION_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SFM.MOD_ID, "inspection"),
            INSPECTION_CHANNEL_VERSION::toString,
            INSPECTION_CHANNEL_VERSION::equals,
            INSPECTION_CHANNEL_VERSION::equals
    );

    public static void register() {
        MANAGER_CHANNEL.registerMessage(
                0,
                ServerboundManagerProgramPacket.class,
                ServerboundManagerProgramPacket::encode,
                ServerboundManagerProgramPacket::decode,
                ServerboundManagerProgramPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                1,
                ServerboundManagerResetPacket.class,
                ServerboundManagerResetPacket::encode,
                ServerboundManagerResetPacket::decode,
                ServerboundManagerResetPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                2,
                ServerboundManagerFixPacket.class,
                ServerboundManagerFixPacket::encode,
                ServerboundManagerFixPacket::decode,
                ServerboundManagerFixPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                3,
                ClientboundManagerGuiUpdatePacket.class,
                ClientboundManagerGuiUpdatePacket::encode,
                ClientboundManagerGuiUpdatePacket::decode,
                ClientboundManagerGuiUpdatePacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                4,
                ServerboundManagerSetLogLevelPacket.class,
                ServerboundManagerSetLogLevelPacket::encode,
                ServerboundManagerSetLogLevelPacket::decode,
                ServerboundManagerSetLogLevelPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                5,
                ServerboundManagerClearLogsPacket.class,
                ServerboundManagerClearLogsPacket::encode,
                ServerboundManagerClearLogsPacket::decode,
                ServerboundManagerClearLogsPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                6,
                ServerboundManagerLogDesireUpdatePacket.class,
                ServerboundManagerLogDesireUpdatePacket::encode,
                ServerboundManagerLogDesireUpdatePacket::decode,
                ServerboundManagerLogDesireUpdatePacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                7,
                ClientboundManagerLogsPacket.class,
                ClientboundManagerLogsPacket::encode,
                ClientboundManagerLogsPacket::decode,
                ClientboundManagerLogsPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                8,
                ServerboundManagerRebuildPacket.class,
                ServerboundManagerRebuildPacket::encode,
                ServerboundManagerRebuildPacket::decode,
                ServerboundManagerRebuildPacket::handle
        );
        MANAGER_CHANNEL.registerMessage(
                9,
                ClientboundManagerLogLevelUpdatedPacket.class,
                ClientboundManagerLogLevelUpdatedPacket::encode,
                ClientboundManagerLogLevelUpdatedPacket::decode,
                ClientboundManagerLogLevelUpdatedPacket::handle
        );


        LABEL_GUN_ITEM_CHANNEL.registerMessage(
                0,
                ServerboundLabelGunUpdatePacket.class,
                ServerboundLabelGunUpdatePacket::encode,
                ServerboundLabelGunUpdatePacket::decode,
                ServerboundLabelGunUpdatePacket::handle
        );
        LABEL_GUN_ITEM_CHANNEL.registerMessage(
                1,
                ServerboundLabelGunPrunePacket.class,
                ServerboundLabelGunPrunePacket::encode,
                ServerboundLabelGunPrunePacket::decode,
                ServerboundLabelGunPrunePacket::handle
        );
        LABEL_GUN_ITEM_CHANNEL.registerMessage(
                2,
                ServerboundLabelGunClearPacket.class,
                ServerboundLabelGunClearPacket::encode,
                ServerboundLabelGunClearPacket::decode,
                ServerboundLabelGunClearPacket::handle
        );
        LABEL_GUN_ITEM_CHANNEL.registerMessage(
                3,
                ServerboundLabelGunUsePacket.class,
                ServerboundLabelGunUsePacket::encode,
                ServerboundLabelGunUsePacket::decode,
                ServerboundLabelGunUsePacket::handle
        );

        DISK_ITEM_CHANNEL.registerMessage(
                0,
                ServerboundDiskItemSetProgramPacket.class,
                ServerboundDiskItemSetProgramPacket::encode,
                ServerboundDiskItemSetProgramPacket::decode,
                ServerboundDiskItemSetProgramPacket::handle
        );

        INSPECTION_CHANNEL.registerMessage(
                0,
                ServerboundContainerExportsInspectionRequestPacket.class,
                ServerboundContainerExportsInspectionRequestPacket::encode,
                ServerboundContainerExportsInspectionRequestPacket::decode,
                ServerboundContainerExportsInspectionRequestPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                1,
                ClientboundContainerExportsInspectionResultsPacket.class,
                ClientboundContainerExportsInspectionResultsPacket::encode,
                ClientboundContainerExportsInspectionResultsPacket::decode,
                ClientboundContainerExportsInspectionResultsPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                2,
                ServerboundLabelInspectionRequestPacket.class,
                ServerboundLabelInspectionRequestPacket::encode,
                ServerboundLabelInspectionRequestPacket::decode,
                ServerboundLabelInspectionRequestPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                3,
                ClientboundLabelInspectionResultsPacket.class,
                ClientboundLabelInspectionResultsPacket::encode,
                ClientboundLabelInspectionResultsPacket::decode,
                ClientboundLabelInspectionResultsPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                4,
                ServerboundInputInspectionRequestPacket.class,
                ServerboundInputInspectionRequestPacket::encode,
                ServerboundInputInspectionRequestPacket::decode,
                ServerboundInputInspectionRequestPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                5,
                ClientboundInputInspectionResultsPacket.class,
                ClientboundInputInspectionResultsPacket::encode,
                ClientboundInputInspectionResultsPacket::decode,
                ClientboundInputInspectionResultsPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                6,
                ServerboundOutputInspectionRequestPacket.class,
                ServerboundOutputInspectionRequestPacket::encode,
                ServerboundOutputInspectionRequestPacket::decode,
                ServerboundOutputInspectionRequestPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                7,
                ClientboundOutputInspectionResultsPacket.class,
                ClientboundOutputInspectionResultsPacket::encode,
                ClientboundOutputInspectionResultsPacket::decode,
                ClientboundOutputInspectionResultsPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                8,
                ServerboundNetworkToolUsePacket.class,
                ServerboundNetworkToolUsePacket::encode,
                ServerboundNetworkToolUsePacket::decode,
                ServerboundNetworkToolUsePacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                9,
                ServerboundIfStatementInspectionRequestPacket.class,
                ServerboundIfStatementInspectionRequestPacket::encode,
                ServerboundIfStatementInspectionRequestPacket::decode,
                ServerboundIfStatementInspectionRequestPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                10,
                ClientboundIfStatementInspectionResultsPacket.class,
                ClientboundIfStatementInspectionResultsPacket::encode,
                ClientboundIfStatementInspectionResultsPacket::decode,
                ClientboundIfStatementInspectionResultsPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                11,
                ServerboundBoolExprStatementInspectionRequestPacket.class,
                ServerboundBoolExprStatementInspectionRequestPacket::encode,
                ServerboundBoolExprStatementInspectionRequestPacket::decode,
                ServerboundBoolExprStatementInspectionRequestPacket::handle
        );
        INSPECTION_CHANNEL.registerMessage(
                12,
                ClientboundBoolExprStatementInspectionResultsPacket.class,
                ClientboundBoolExprStatementInspectionResultsPacket::encode,
                ClientboundBoolExprStatementInspectionResultsPacket::decode,
                ClientboundBoolExprStatementInspectionResultsPacket::handle
        );
    }

    public static <MENU extends AbstractContainerMenu, BE extends BlockEntity> void handleServerboundContainerPacket(
            @Nullable Supplier<NetworkEvent.Context> ctxSupplier,
            Class<MENU> menuClass,
            Class<BE> blockEntityClass,
            BlockPos pos,
            int containerId,
            BiConsumer<MENU, BE> callback
    ) {
        if (ctxSupplier == null) return;

        var ctx = ctxSupplier.get();
        if (ctx == null) return;
        // TODO: log return cases about invalid packet received
        ctx.enqueueWork(() -> {
            var sender = ctx.getSender();
            if (sender == null) return;
            if (sender.isSpectator()) return; // ignore packets from spectators

            var menu = sender.containerMenu;
            if (!menuClass.isInstance(menu)) return;
            if (menu.containerId != containerId) return;

            var level = sender.getLevel();
            //noinspection ConstantValue
            if (level == null) return;
            if (!level.isLoaded(pos)) return;

            var blockEntity = level.getBlockEntity(pos);
            if (!blockEntityClass.isInstance(blockEntity)) return;
            //noinspection unchecked
            callback.accept((MENU) menu, (BE) blockEntity);
        });
    }
}
