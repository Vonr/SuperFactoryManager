/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import java.util.Optional;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class FlowDataSerializer<T extends FlowData> extends
	ForgeRegistryEntry<FlowDataSerializer<?>> {

	public static final String NBT_STAMP_KEY = "factory_registry_name";
	private static IForgeRegistry cached = null;

	public FlowDataSerializer(ResourceLocation registryName) {
		setRegistryName(registryName);
	}

	public static Optional<FlowDataSerializer<?>> getSerializer(CompoundNBT tag) {
		if (!tag.contains(NBT_STAMP_KEY, NBT.TAG_STRING)) {
			return Optional.empty();
		}
		return getSerializer(tag.getString(NBT_STAMP_KEY));
	}

	public static Optional<FlowDataSerializer<?>> getSerializer(ResourceLocation id) {
		if (cached == null) {
			cached = GameRegistry.findRegistry(FlowDataSerializer.class);
		}
		return Optional.ofNullable(cached).map(r -> ((FlowDataSerializer) r.getValue(id)));
	}

	public static Optional<FlowDataSerializer<?>> getSerializer(String id) {
		return getSerializer(ResourceLocation.tryCreate(id));
	}

	public abstract T fromNBT(CompoundNBT tag);

	public CompoundNBT toNBT(T data) {
		CompoundNBT tag = new CompoundNBT();
		//noinspection ConstantConditions
		tag.putString(NBT_STAMP_KEY, getRegistryName().toString());
		tag.putString("uuid", data.getId().toString());
		return tag;
	}

	public abstract T fromBuffer(PacketBuffer buf);

	public abstract void toBuffer(T data, PacketBuffer buf);

}
