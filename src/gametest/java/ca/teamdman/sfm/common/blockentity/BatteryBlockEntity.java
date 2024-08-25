package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.registry.SFMTestBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryBlockEntity extends BlockEntity {

    public final IEnergyStorage               CONTAINER  = new EnergyStorage(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE
    );
    public final LazyOptional<IEnergyStorage> CAPABILITY = LazyOptional.of(() -> CONTAINER);

    public BatteryBlockEntity(
            BlockPos pPos,
            BlockState pBlockState
    ) {
        super(SFMTestBlockEntities.BATTERY_BLOCK_ENTITY.get(), pPos, pBlockState);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return CAPABILITY.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public void invalidateCaps() {
        CAPABILITY.invalidate();
    }
}
