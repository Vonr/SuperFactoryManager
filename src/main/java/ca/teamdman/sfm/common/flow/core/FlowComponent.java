package ca.teamdman.sfm.common.flow.core;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class FlowComponent extends net.minecraftforge.registries.ForgeRegistryEntry<FlowComponent> implements INBTSerializable<CompoundNBT> {
	public FlowComponent(ResourceLocation name) {
		setRegistryName(name);
	}
}
