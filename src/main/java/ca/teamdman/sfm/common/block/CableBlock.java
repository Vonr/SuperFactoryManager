package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.blockentity.CableBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.cablenetwork.ICableBlock;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.FacadeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;


public class CableBlock extends Block implements ICableBlock, EntityBlock {
    public static final ModelProperty<BlockState> FACADE_BLOCK_STATE = new ModelProperty<>();
    public static final EnumProperty<FacadeType> FACADE_TYPE_PROP = FacadeType.FACADE_TYPE;

    public CableBlock() {
        super(Properties.of()
                .instrument(NoteBlockInstrument.BASS)
                .destroyTime(1f)
                .sound(SoundType.METAL)
        );

        registerDefaultState(stateDefinition.any()
                .setValue(FACADE_TYPE_PROP, FacadeType.NONE)
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        CableNetworkManager.onCablePlaced(world, pos);
    }

/*
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pLevel.isClientSide() || pPlacer == null) return;
        if (pPlacer instanceof Player pPlayer) {
            ItemStack offHandItemStack = pPlacer.getItemInHand(InteractionHand.OFF_HAND);

            setFacade(offHandItemStack, pLevel, pState, pPos, pPlayer, InteractionHand.OFF_HAND, null);
        }
    }
*/

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);

        CableNetworkManager.onCableRemoved(pLevel, pPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        Item offHandItem = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getItem();
        if (offHandItem == SFMItems.NETWORK_TOOL_ITEM.get()) {
            return setFacade(pStack, pLevel, pState, pPos, pPlayer, pHand, pHitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemInteractionResult setFacade(
            ItemStack pStack,
            Level pLevel,
            BlockState pState,
            BlockPos pPos,
            Player pPlayer,
            InteractionHand pHand,
            @Nullable BlockHitResult pHitResult
    ) {
        Block block = itemStackToBlock(pStack, pLevel, pPos);
        if (block == null) return ItemInteractionResult.FAIL;

        CableBlockEntity blockEntity = getCableBlockEntity(pLevel, pPos);
        if (blockEntity == null) return ItemInteractionResult.FAIL;
        if (block == this) {
            pLevel.removeBlockEntity(pPos);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(FACADE_TYPE_PROP, FacadeType.NONE));
        } else {
            if (pHitResult != null) {
                // Support for block rotation
                BlockPlaceContext blockPlaceContext = new BlockPlaceContext(pPlayer, pHand, pStack, pHitResult);
                BlockState placedState = block.getStateForPlacement(blockPlaceContext);

                if (placedState == null) return ItemInteractionResult.FAIL;
                blockEntity.setFacadeState(placedState);
            } else { // Should never be reached because setPlacedBy is commented out
                blockEntity.setFacadeState(block.defaultBlockState());
            }

            // If facade block is not solid then set TRANSLUCENT block state
            boolean isFacadeTranslucent = !blockEntity.getFacadeState().isSolidRender(pLevel, pPos);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(FACADE_TYPE_PROP, isFacadeTranslucent ? FacadeType.TRANSLUCENT_FACADE : FacadeType.OPAQUE_FACADE));
        }
        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACADE_TYPE_PROP);
    }

    private @Nullable Block itemStackToBlock(ItemStack itemStack, Level pLevel, BlockPos pPos) {
        // Empty hand should just return an SFM Cable, lets us delete the block entity
        Item item = itemStack.getItem();
        if (item == Items.AIR)
            return this;
        // Full block should return block resource, update facade
        Block block = Block.byItem(item);
        BlockState blockState = block.defaultBlockState();

        if (blockState.isCollisionShapeFullBlock(pLevel, pPos)) {
            return block;
        }
        // Non-full block or item should return null, do nothing
        return null;
    }

    private @Nullable CableBlockEntity getCableBlockEntity(Level pLevel, BlockPos pPos) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        // Return existing block entity
        if (blockEntity != null) return (CableBlockEntity) blockEntity;

        // Create new block entity
        CableBlockEntity cableBlockEntity = SFMBlockEntities.CABLE_BLOCK_ENTITY.get().create(pPos, defaultBlockState());
        if (cableBlockEntity == null) return null;
        pLevel.setBlockEntity(cableBlockEntity);
        return cableBlockEntity;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        // Create block entity if FACADE_TYPE is not NONE
        return blockState.getValue(FACADE_TYPE_PROP) != FacadeType.NONE ?
                SFMBlockEntities.CABLE_BLOCK_ENTITY.get().create(blockPos, blockState) :
                null;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        // Translucent blocks should have no occlusion
        return pState.getValue(FACADE_TYPE_PROP) == FacadeType.TRANSLUCENT_FACADE ?
                Shapes.empty() :
                Shapes.block();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getValue(FACADE_TYPE_PROP) == FacadeType.TRANSLUCENT_FACADE;
    }
}
