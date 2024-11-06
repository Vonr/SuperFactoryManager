package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.block.CableBlock;
import ca.teamdman.sfm.common.block.CableFacadeBlock;
import ca.teamdman.sfm.common.blockentity.CableFacadeBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
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
        // Get cable
        Level level = sender.level;
        BlockPos cablePos = msg.pHitResult.getBlockPos();
        if (!level.isLoaded(cablePos)) return;
        BlockState cableBlockState = level.getBlockState(cablePos);
        if (!(cableBlockState.getBlock() instanceof CableBlock)) return;

        // Get paint
        ItemStack paintStack = sender.getMainHandItem();
        Block paintBlock = getBlockFromStack(
                paintStack,
                level,
                cablePos
        );
        if (paintBlock == null) return;

        // Perform update
        boolean clearPaint = paintBlock == SFMBlocks.CABLE_BLOCK.get();
        if (clearPaint) {
            // Set to normal cable
            BlockState newBlockState = SFMBlocks.CABLE_BLOCK.get().defaultBlockState();
            gatherCableBlocksToFacade(msg.spreadLogic, level, cablePos)
                    .filter(pos -> level.getBlockState(pos).getBlock() instanceof CableFacadeBlock)
                    .forEach(blockPos -> level.setBlock(
                            blockPos,
                            newBlockState,
                            Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS
                    ));
        } else {
            // Set or update facade
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(
                    sender,
                    InteractionHand.MAIN_HAND,
                    paintStack,
                    msg.pHitResult
            );
            BlockState paintBlockState = Objects.requireNonNullElse(
                    paintBlock.getStateForPlacement(blockPlaceContext),
                    paintBlock.defaultBlockState()
            );
            FacadeType facadeType = paintBlockState.isSolidRender(level, cablePos)
                                    ? FacadeType.OPAQUE
                                    : FacadeType.TRANSLUCENT;
            BlockState newBlockState = SFMBlocks.CABLE_FACADE_BLOCK.get()
                    .defaultBlockState()
                    .setValue(CableFacadeBlock.FACADE_TYPE_PROP, facadeType);

            gatherCableBlocksToFacade(msg.spreadLogic, level, cablePos)
                    .filter(pos -> level.getBlockState(pos).getBlock() instanceof CableBlock)
                    .forEach(blockPos -> {
                        BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        if (blockEntity != null) {
                            if (blockEntity instanceof CableFacadeBlockEntity found) {
                                found.setFacadeState(paintBlockState);
                            }
                        } else {
                            level.setBlock(blockPos, newBlockState, Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS);
                            if (level.getBlockEntity(blockPos) instanceof CableFacadeBlockEntity cableFacadeBlockEntity) {
                                cableFacadeBlockEntity.setFacadeState(paintBlockState);
                            }
                        }
                    });
        }


    }

    public static Stream<BlockPos> gatherCableBlocksToFacade(
            SpreadLogic spreadLogic,
            Level level,
            BlockPos startCablePos
    ) {
        return switch (spreadLogic) {
            case SINGLE -> Stream.of(startCablePos);
            case NETWORK -> CableNetwork.discoverCables(level, startCablePos);
            case NETWORK_GLOBAL_SAME_BLOCK -> {
                Block check = (level.getBlockEntity(startCablePos) instanceof CableFacadeBlockEntity cableBlockEntity)
                              ? cableBlockEntity.getFacadeState().getBlock()
                              : null;
                yield CableNetwork.discoverCables(level, startCablePos)
                        .filter(
                                cablePos -> level.getBlockEntity(cablePos) instanceof CableFacadeBlockEntity otherCableFacadeBlockEntity
                                            ? otherCableFacadeBlockEntity.getFacadeState().getBlock() == check
                                            : check == null
                        );
            }
            case NETWORK_CONTIGUOUS_SAME_BLOCK -> {
                Set<BlockPos> cablePositions = CableNetwork.discoverCables(level, startCablePos)
                        .collect(Collectors.toSet());
                Block check = (level.getBlockEntity(startCablePos) instanceof CableFacadeBlockEntity cableBlockEntity)
                              ? cableBlockEntity.getFacadeState().getBlock()
                              : null;
                yield SFMUtils.getRecursiveStream((current, next, results) -> {
                    results.accept(current);
                    SFMUtils.get3DNeighboursIncludingKittyCorner(current).forEach(neighbour -> {
                        if (
                                cablePositions.contains(neighbour)
                                && (
                                        level.getBlockEntity(neighbour) instanceof CableFacadeBlockEntity otherCableFacadeBlockEntity
                                        ? otherCableFacadeBlockEntity.getFacadeState().getBlock() == check
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

    public static @Nullable Block getBlockFromStack(
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
