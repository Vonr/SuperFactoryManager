package ca.teamdman.sfm.common.flowdata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class FlowDataFactory<T extends FlowData> extends
	net.minecraftforge.registries.ForgeRegistryEntry<FlowDataFactory<?>> {

	public FlowDataFactory(ResourceLocation registryName) {
		setRegistryName(registryName);
	}

	public boolean matches(CompoundNBT tag) {
		if (!tag.contains("factory_registry_name", NBT.TAG_STRING)) {
			return false;
		}
		String name = tag.getString("factory_registry_name");
		return name.equals(getRegistryName().toString());
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
