package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.block.WaterTankBlock;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterTankBlockEntity extends BlockEntity {
    public void setConnectedCount(int connectedCount) {
        TANK.setCapacity(connectedCount * 1000);
        TANK.getFluid().setAmount(TANK.getCapacity());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ((WaterTankBlock) getBlockState().getBlock()).recount(getLevel(), getBlockPos());
    }

    // can't fill, only extract
    public final FluidTank TANK = new FluidTank(1000, fluidStack -> false) {
        {
            setFluid(new FluidStack(Fluids.WATER, 1000));
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            if (!getLevel().getBlockState(getBlockPos()).getValue(WaterTankBlock.IN_WATER)) return FluidStack.EMPTY;
            int        drained = Math.min(maxDrain, TANK.getCapacity());
            FluidStack copy    = fluid.copy();
            copy.setAmount(drained);
            return copy;
        }
    };

    public final LazyOptional<IFluidHandler> TANK_CAPABILITY = LazyOptional.of(() -> TANK);

    public WaterTankBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(SFMBlockEntities.WATER_TANK_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return TANK_CAPABILITY.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Override
    public void invalidateCaps() {
        TANK_CAPABILITY.invalidate();
    }
}
