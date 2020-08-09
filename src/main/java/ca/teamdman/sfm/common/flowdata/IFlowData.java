package ca.teamdman.sfm.common.flowdata;

import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IFlowData extends INBTSerializable<CompoundNBT> {
	UUID getId();

	FlowData copy();
}
