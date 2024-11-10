package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SFMPacketHandlingContext {
    private final NetworkEvent.Context inner;

    public SFMPacketHandlingContext(Supplier<NetworkEvent.Context> inner) {
        this.inner = inner.get();
    }

    public @Nullable ServerPlayer sender() {
        return inner.getSender();
    }

    public void finish() {
        inner.setPacketHandled(true);
    }

    public void enqueueAndFinish(Runnable runnable) {
        inner.enqueueWork(runnable);
        finish();
    }

    public <MENU extends AbstractContainerMenu, BE extends BlockEntity> void handleServerboundContainerPacket(
            Class<MENU> menuClass,
            Class<BE> blockEntityClass,
            BlockPos pos,
            int containerId,
            BiConsumer<MENU, BE> callback
    ) {
        handleServerboundContainerPacket(
                this,
                menuClass,
                blockEntityClass,
                pos,
                containerId,
                callback
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

    public static <MENU extends AbstractContainerMenu, BE extends BlockEntity> void handleServerboundContainerPacket(
            SFMPacketHandlingContext ctx,
            Class<MENU> menuClass,
            Class<BE> blockEntityClass,
            BlockPos pos,
            int containerId,
            BiConsumer<MENU, BE> callback
    ) {
        // TODO: log return cases about invalid packet received
        var sender = ctx.sender();
        if (sender == null) return;
        if (sender.isSpectator()) return;

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
    }

    public void compileAndThen(
            String programString,
            ProgramConsumer callback
    ) {
        ServerPlayer player = this.sender();
        if (player == null) return;
        ManagerBlockEntity manager;
        if (player.containerMenu instanceof ManagerContainerMenu mcm) {
            if (player.getLevel().getBlockEntity(mcm.MANAGER_POSITION) instanceof ManagerBlockEntity mbe) {
                manager = mbe;
            } else {
                return;
            }
        } else {
            //todo: localize
            SFMPackets.sendToPlayer(() -> player, new ClientboundInputInspectionResultsPacket(
                    "This inspection is only available when editing inside a manager."));
            return;
        }
        Program.compile(
                programString,
                successProgram -> callback.accept(successProgram, player, manager),
                failure -> {
                    //todo: translate
                    SFMPackets.sendToPlayer(
                            () -> player,
                            new ClientboundOutputInspectionResultsPacket("failed to compile program")
                    );
                }
        );
    }

    @FunctionalInterface
    public interface ProgramConsumer {
        void accept(
                Program program,
                ServerPlayer player,
                ManagerBlockEntity managerBlockEntity
        );
    }
}
