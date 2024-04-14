package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BatteryBlockEntity extends BlockEntity {

    public final IEnergyStorage CONTAINER = new EnergyStorage(
            Integer.MAX_VALUE,
            Integer.MAX_VALUE,
            Integer.MAX_VALUE
    );

    public BatteryBlockEntity(
            BlockPos pPos,
            BlockState pBlockState
    ) {
        super(SFMBlockEntities.BATTERY_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
