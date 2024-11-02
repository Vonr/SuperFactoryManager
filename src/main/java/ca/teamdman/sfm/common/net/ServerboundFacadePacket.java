package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.block.CableBlock;
import ca.teamdman.sfm.common.blockentity.CableBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.util.FacadeType;
import ca.teamdman.sfm.common.util.SFMUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ServerboundFacadePacket(
        BlockHitResult pHitResult,
        SpreadLogic spreadLogic
) {
    private static final BlockState defaultCableState = SFMBlocks.CABLE_BLOCK.get().defaultBlockState();

    public static void encode(
            ServerboundFacadePacket msg,
            FriendlyByteBuf buf
    ) {
        buf.writeBlockHitResult(msg.pHitResult);
        buf.writeEnum(msg.spreadLogic);
    }

    public static ServerboundFacadePacket decode(FriendlyByteBuf buf) {
        return new ServerboundFacadePacket(
                buf.readBlockHitResult(),
                buf.readEnum(SpreadLogic.class)
        );
    }

    public static void handle(
            ServerboundFacadePacket msg,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            // CTRL         :   Connected and Matching
            // ALT          :   Matching
            // CTRL + ALT   :   All
            Player sender = contextSupplier.get().getSender();
            if (sender == null) return;

            handle(msg, sender);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static void handle(
            ServerboundFacadePacket msg,
            Player sender
    ) {
        Level level = sender.level;
        BlockPos hitBlockPos = msg.pHitResult.getBlockPos();
        if (!level.isLoaded(hitBlockPos)) return;
        ItemStack itemInHand = sender.getMainHandItem();

        Block newFacadeBlock = getBlockFromStack(
                itemInHand,
                level,
                hitBlockPos
        );
        if (newFacadeBlock == null) return;

        BlockState placedFacadeState = Objects.requireNonNullElse(
                newFacadeBlock.getStateForPlacement(new BlockPlaceContext(
                        sender,
                        InteractionHand.MAIN_HAND,
                        itemInHand,
                        msg.pHitResult
                )),
                newFacadeBlock.defaultBlockState()
        );

        BlockState hitBlockState = level.getBlockState(hitBlockPos);
        if (msg.spreadLogic != SpreadLogic.NETWORK) {
            // Early return for trying to set the same facade to the same block state
            BlockState hitFacadeState = level.getBlockEntity(hitBlockPos) instanceof CableBlockEntity cableBlockEntity
                                        ? cableBlockEntity.getFacadeState()
                                        : null;
            boolean sameState = hitFacadeState == placedFacadeState;
            boolean willBeCable = hitBlockState == defaultCableState && newFacadeBlock == SFMBlocks.CABLE_BLOCK.get();
            if (sameState || willBeCable) return;
        }

        FacadeType facadeType = newFacadeBlock == SFMBlocks.CABLE_BLOCK.get()
                                ? FacadeType.NONE
                                : placedFacadeState.isSolidRender(level, hitBlockPos)
                                  ? FacadeType.OPAQUE_FACADE
                                  : FacadeType.TRANSLUCENT_FACADE;
        BlockState newBlockState = hitBlockState.setValue(CableBlock.FACADE_TYPE_PROP, facadeType);
        gatherCableBlocksToFacade(msg.spreadLogic, level, hitBlockPos)
                .forEach(blockPos -> {
                    CableBlockEntity cableBlockEntity = getCableBlockEntity(level, blockPos);
                    if (cableBlockEntity == null) return;

                    if (newFacadeBlock == SFMBlocks.CABLE_BLOCK.get()) {
                        cableBlockEntity.setFacadeState(defaultCableState);
                        level.removeBlockEntity(blockPos);
                    } else {
                        cableBlockEntity.setFacadeState(placedFacadeState);
                    }
                    level.setBlock(blockPos, newBlockState, Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS);
                });
    }

    private static Stream<BlockPos> gatherCableBlocksToFacade(
            SpreadLogic spreadLogic,
            Level level,
            BlockPos startCablePos
    ) {
        return switch (spreadLogic) {
            case SINGLE -> Stream.of(startCablePos);
            case NETWORK -> CableNetworkManager.getContiguousCables(level, startCablePos);
            case NETWORK_GLOBAL_SAME_BLOCK -> {
                Block check = (level.getBlockEntity(startCablePos) instanceof CableBlockEntity cableBlockEntity)
                              ? cableBlockEntity.getFacadeState().getBlock()
                              : null;
                yield CableNetworkManager
                        .getContiguousCables(level, startCablePos)
                        .filter(
                                cablePos -> level.getBlockEntity(cablePos) instanceof CableBlockEntity otherCableBlockEntity
                                            ? otherCableBlockEntity.getFacadeState().getBlock() == check
                                            : check == null
                        );
            }
            case NETWORK_CONTIGUOUS_SAME_BLOCK -> {
                Set<BlockPos> cablePositions = CableNetworkManager
                        .getContiguousCables(level, startCablePos)
                        .collect(Collectors.toSet());
                Block check = (level.getBlockEntity(startCablePos) instanceof CableBlockEntity cableBlockEntity)
                              ? cableBlockEntity.getFacadeState().getBlock()
                              : null;
                yield SFMUtils.getRecursiveStream((current, next, results) -> {
                    results.accept(current);
                    SFMUtils.get3DNeighboursIncludingKittyCorner(current).forEach(neighbour -> {
                        if (
                                cablePositions.contains(neighbour)
                                && (
                                        level.getBlockEntity(neighbour) instanceof CableBlockEntity otherCableBlockEntity
                                        ? otherCableBlockEntity.getFacadeState().getBlock() == check
                                        : check == null
                                )
                        ) {
                            next.accept(neighbour);
                        }
                    });
                }, startCablePos);
            }
        };
    }

    private static @Nullable Block getBlockFromStack(
            ItemStack itemStack,
            Level level,
            BlockPos pos
    ) {
        // Empty hand should just return an SFM Cable, lets us delete the block entity
        Item item = itemStack.getItem();
        if (item == Items.AIR) {
            return SFMBlocks.CABLE_BLOCK.get();
        }
        // Full block should return block resource, update facade
        Block block = Block.byItem(item);
        BlockState blockState = block.defaultBlockState();

        if (blockState.isCollisionShapeFullBlock(level, pos)) {
            return block;
        }
        // Non-full block or item should return null, do nothing
        return null;
    }

    private static @Nullable CableBlockEntity getCableBlockEntity(
            Level pLevel,
            BlockPos pPos
    ) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        // Return existing block entity
        if (blockEntity != null) return (CableBlockEntity) blockEntity;

        // Create new block entity
        CableBlockEntity cableBlockEntity = SFMBlockEntities.CABLE_BLOCK_ENTITY.get().create(pPos, defaultCableState);
        if (cableBlockEntity == null) return null;
        pLevel.setBlockEntity(cableBlockEntity);
        return cableBlockEntity;
    }

    public enum SpreadLogic {
        SINGLE,
        NETWORK,
        NETWORK_GLOBAL_SAME_BLOCK,
        NETWORK_CONTIGUOUS_SAME_BLOCK;

        public static SpreadLogic fromParts(
                boolean isCtrlKeyDown,
                boolean isAltKeyDown
        ) {
            if (isCtrlKeyDown && isAltKeyDown) {
                return NETWORK;
            }
            if (isAltKeyDown) {
                return NETWORK_GLOBAL_SAME_BLOCK;
            }
            if (isCtrlKeyDown) {
                return NETWORK_CONTIGUOUS_SAME_BLOCK;
            }
            return SINGLE;
        }
    }
}
