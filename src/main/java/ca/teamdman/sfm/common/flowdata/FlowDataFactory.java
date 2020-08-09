package ca.teamdman.sfm.common.flowdata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class FlowDataFactory<T extends FlowData> extends
	net.minecraftforge.registries.ForgeRegistryEntry<FlowDataFactory<?>> {

	public FlowDataFactory(ResourceLocation registryName) {
		setRegistryName(registryName);
	}

	public static final String NBT_STAMP_KEY = "factory_registry_name";

	public boolean matches(CompoundNBT tag) {
		if (!tag.contains(NBT_STAMP_KEY, NBT.TAG_STRING)) {
			return false;
		}
		String name = tag.getString(NBT_STAMP_KEY);
		return name.equals(getRegistryName().toString());
	}

	public void stampNBT(CompoundNBT tag) {
		tag.putString(NBT_STAMP_KEY, getRegistryName().toString());
	}

	public T fromNBT(CompoundNBT tag) {
		return null;
	}

	public static final class DummyFlowDataFactory extends FlowDataFactory {

		public DummyFlowDataFactory(ResourceLocation key) {
			super(key);
		}
	}

	public static final class MissingFlowDataFactory extends FlowDataFactory {

		public MissingFlowDataFactory(ResourceLocation key, boolean isNetwork) {
			super(key);
		}
	}
}
