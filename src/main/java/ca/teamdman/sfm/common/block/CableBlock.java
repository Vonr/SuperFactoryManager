package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.cablenetwork.ICableBlock;
import ca.teamdman.sfm.common.net.ServerboundFacadePacket;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.FacadeType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;


public class CableBlock extends Block implements ICableBlock, EntityBlock {
    public static final ModelProperty<BlockState> FACADE_BLOCK_STATE = new ModelProperty<>();
    public static final EnumProperty<FacadeType> FACADE_TYPE_PROP = FacadeType.FACADE_TYPE;

    public CableBlock() {
        super(Block.Properties
                      .of(Material.METAL)
                      .destroyTime(1f)
                      .sound(SoundType.METAL));
        registerDefaultState(stateDefinition.any()
                                     .setValue(FACADE_TYPE_PROP, FacadeType.NONE)
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(
            BlockState state,
            Level world,
            BlockPos pos,
            BlockState oldState,
            boolean isMoving
    ) {
        CableNetworkManager.onCablePlaced(world, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(
            BlockState pState,
            Level pLevel,
            BlockPos pPos,
            BlockState pNewState,
            boolean pIsMoving
    ) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        if (!(pNewState.getBlock() instanceof ICableBlock)) {
            CableNetworkManager.onCableRemoved(pLevel, pPos);
        }
    }

    @Override
    public InteractionResult use(
            BlockState pState,
            Level pLevel,
            BlockPos pPos,
            Player pPlayer,
            InteractionHand pHand,
            BlockHitResult pHit
    ) {
        if (pPlayer.getOffhandItem().getItem() == SFMItems.NETWORK_TOOL_ITEM.get()) {
            if (pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND) {
                ServerboundFacadePacket msg = new ServerboundFacadePacket(
                        pHit,
                        ServerboundFacadePacket.SpreadLogic.fromParts(Screen.hasControlDown(), Screen.hasAltDown())
                );
                SFMPackets.CABLE_CHANNEL.sendToServer(msg);

                // eagerly handle the update on the client for immediate feedback
                ClientStuff.eagerExecute(msg);

                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        // Create block entity if FACADE_TYPE is not NONE
        return blockState.getValue(FACADE_TYPE_PROP) != FacadeType.NONE ?
               SFMBlockEntities.CABLE_BLOCK_ENTITY.get().create(blockPos, blockState) :
               null;
    }

    @Override
    public VoxelShape getOcclusionShape(
            BlockState pState,
            BlockGetter pLevel,
            BlockPos pPos
    ) {
        // Translucent blocks should have no occlusion
        return pState.getValue(FACADE_TYPE_PROP) == FacadeType.TRANSLUCENT_FACADE ?
               Shapes.empty() :
               Shapes.block();
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState pState,
            BlockGetter pLevel,
            BlockPos pPos
    ) {
        return pState.getValue(FACADE_TYPE_PROP) == FacadeType.TRANSLUCENT_FACADE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACADE_TYPE_PROP);
    }
}
