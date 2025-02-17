package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.block.CableFacadeBlock;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class CableFacadeBlockEntity extends BlockEntity {
    private BlockState facadeState = SFMBlocks.CABLE_FACADE_BLOCK.get().defaultBlockState();

    public CableFacadeBlockEntity(BlockPos pos, BlockState state) {
        super(SFMBlockEntities.CABLE_FACADE_BLOCK_ENTITY.get(), pos, state);
    }

    public BlockState getFacadeState() {
        return facadeState;
    }

    public void setFacadeState(BlockState newFacadeState) {
        if (newFacadeState == facadeState) return;

        this.facadeState = newFacadeState;
        setChanged();
        if (level != null) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, facadeState, Block.UPDATE_IMMEDIATE);
        }
        requestModelDataUpdate();
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(CableFacadeBlock.FACADE_BLOCK_STATE, facadeState).build();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (facadeState.getBlock() != SFMBlocks.CABLE_BLOCK.get()) {
            pTag.put("sfm:facade", NbtUtils.writeBlockState(facadeState));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("sfm:facade")) {
            facadeState = NbtUtils.readBlockState(pTag.getCompound("sfm:facade"));
            requestModelDataUpdate();
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag pTag = new CompoundTag();
        saveAdditional(pTag);
        return pTag;
    }
}
