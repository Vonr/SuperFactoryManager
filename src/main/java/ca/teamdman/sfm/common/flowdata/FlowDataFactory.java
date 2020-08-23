package ca.teamdman.sfm.common.flowdata;

import java.util.Optional;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class FlowDataFactory<T extends FlowData> extends
	net.minecraftforge.registries.ForgeRegistryEntry<FlowDataFactory<?>> {

	public static final String NBT_STAMP_KEY = "factory_registry_name";

	public FlowDataFactory(ResourceLocation registryName) {
		setRegistryName(registryName);
	}

	public static Optional<FlowDataFactory<?>> getFactory(CompoundNBT tag) {
		if (!tag.contains(NBT_STAMP_KEY, NBT.TAG_STRING)) {
			return Optional.empty();
		}
		ResourceLocation id = ResourceLocation.tryCreate(tag.getString(NBT_STAMP_KEY));
		if (id == null) {
			return Optional.empty();
		}
		return getFactory(id);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Optional<IForgeRegistry<? extends FlowDataFactory>> getRegistry() {
		return Optional.of(GameRegistry.findRegistry(FlowDataFactory.class));
	}

	@SuppressWarnings("unchecked")
	public static Optional<FlowDataFactory<?>> getFactory(ResourceLocation key) {
		return getRegistry().map(r -> r.getValue(key));
	}

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

}
