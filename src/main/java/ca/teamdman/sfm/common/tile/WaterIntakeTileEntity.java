package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.registrar.SFMTiles;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class WaterIntakeTileEntity extends TileEntity {

	public final FluidTank TANK = new FluidTank(
		1000,
		stack -> false // can't fill, only extract
	) {
		{
			setFluid(new FluidStack(Fluids.WATER, 1000));
		}

		@Nonnull
		@Override
		public FluidStack drain(
			int maxDrain, FluidAction action
		) {
			int drained = Math.min(maxDrain, 1000);
			FluidStack copy = fluid.copy();
			copy.setAmount(drained);
			return copy;
		}
	};
	public final LazyOptional<IFluidHandler> TANK_CAPABILITY = LazyOptional.of(
		() -> TANK);

	public WaterIntakeTileEntity() {
		super(SFMTiles.WATER_INTAKE.get());
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(
		@Nonnull Capability<T> cap, @Nullable Direction side
	) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return TANK_CAPABILITY.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void remove() {
		super.remove();
		TANK_CAPABILITY.invalidate();
	}
}
