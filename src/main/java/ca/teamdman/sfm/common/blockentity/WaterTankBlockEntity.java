package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.block.WaterTankBlock;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class WaterTankBlockEntity extends BlockEntity {
    public void setConnectedCount(int connectedCount) {
        TANK.setCapacity(connectedCount * 1000);
        TANK.getFluid().setAmount(TANK.getCapacity());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //noinspection DataFlowIssue
        ((WaterTankBlock) getBlockState().getBlock()).recount(getLevel(), getBlockPos());
    }

    // can't fill, only extract
    public final FluidTank TANK = new FluidTank(1000, fluidStack -> false) {
        {
            setFluid(new FluidStack(Fluids.WATER, 1000));
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            //noinspection DataFlowIssue
            if (!getLevel().getBlockState(getBlockPos()).getValue(WaterTankBlock.IN_WATER)) return FluidStack.EMPTY;
            int        drained = Math.min(maxDrain, TANK.getCapacity());
            FluidStack copy    = fluid.copy();
            copy.setAmount(drained);
            return copy;
        }
    };

    public WaterTankBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(SFMBlockEntities.WATER_TANK_BLOCK_ENTITY.get(), pos, state);
    }
}
