/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class FlowDataSerializer<T extends FlowData> extends
	ForgeRegistryEntry<FlowDataSerializer<?>> {

	public static final String NBT_SERIALIZER_REGISTRY_NAME_KEY = "__type";
	public static final String NBT_SERIALIZER_SCHEMA_VERSION_KEY = "__version";
	public static final String NBT_SERIALIZER_UUID_KEY = "__uuid";
	private static IForgeRegistry cached = null;

	public FlowDataSerializer(ResourceLocation registryName) {
		setRegistryName(registryName);
	}

	public static UUID getUUID(CompoundNBT tag) {
		if (tag.contains(NBT_SERIALIZER_UUID_KEY, NBT.TAG_STRING)) {
			return UUID.fromString(tag.getString(NBT_SERIALIZER_UUID_KEY));
		} else {
			throw new IllegalArgumentException("tag doesn't contain a uuid");
		}
	}

	public static Optional<FlowData> deserialize(CompoundNBT tag) {
		Optional<FlowDataSerializer<?>> serializer = getSerializer(tag);
		if (!serializer.isPresent()) {
			SFM.LOGGER.warn(
				SFMUtil.getMarker(FlowDataSerializer.class),
				"Could not find serializer for tag {}",
				tag
			);
			return Optional.empty();
		}
		try {
			FlowData data = ((FlowDataSerializer<FlowData>) serializer.get())
				.fromPossiblyOutdatedNBT(tag);
			return Optional.of(data);
		} catch (Exception e) {
			SFM.LOGGER.error(
				SFMUtil.getMarker(FlowDataSerializer.class),
				"Error deserializing flow data from {}",
				tag,
				e
			);
		}
		return Optional.empty();
	}

	public static Optional<FlowDataSerializer<?>> getSerializer(CompoundNBT tag) {
		if (!tag.contains(NBT_SERIALIZER_REGISTRY_NAME_KEY, NBT.TAG_STRING)) {
			return Optional.empty();
		}
		return getSerializer(tag.getString(NBT_SERIALIZER_REGISTRY_NAME_KEY));
	}

	/**
	 * Applies any schema updates to the tag, then converts it to the correct FlowData
	 *
	 * @param tag possibly outdated tag
	 * @return FlowData
	 */
	public T fromPossiblyOutdatedNBT(CompoundNBT tag) {
		tag = getWithLatestSchema(tag, getVersion(tag));
		if (getVersion(tag) != getVersion()) {
			throw new IllegalArgumentException("tag schema not latest after updating");
		}
		return fromNBT(tag);
	}

	public static Optional<FlowDataSerializer<?>> getSerializer(String id) {
		return getSerializer(ResourceLocation.tryCreate(id));
	}

	/**
	 * @param tag            Serialized FlowData
	 * @param currentVersion Schema version used to write the tag
	 * @return Serialized FlowData using latest schema
	 */
	public CompoundNBT getWithLatestSchema(CompoundNBT tag, int currentVersion) {
		return tag;
	}

	/**
	 * @param tag Serialized FlowData
	 * @return Schema version used to write the tag
	 */
	public static int getVersion(CompoundNBT tag) {
		if (tag.contains(NBT_SERIALIZER_SCHEMA_VERSION_KEY, NBT.TAG_INT)) {
			return tag.getInt(NBT_SERIALIZER_SCHEMA_VERSION_KEY);
		} else {
			throw new IllegalArgumentException("tag doesn't contain a schema version");
		}
	}

	/**
	 * @return Schema version
	 */
	public int getVersion() {
		return 1;
	}

	/**
	 * @param tag Serialized FlowData using latest schema
	 * @return Deserialized FlowData
	 */
	public abstract T fromNBT(CompoundNBT tag);

	public static Optional<FlowDataSerializer<?>> getSerializer(ResourceLocation id) {
		if (cached == null) {
			cached = GameRegistry.findRegistry(FlowDataSerializer.class);
		}
		return Optional.ofNullable(cached).map(r -> ((FlowDataSerializer) r.getValue(id)));
	}

	public CompoundNBT toNBT(T data) {
		CompoundNBT tag = new CompoundNBT();

		// write metadata to tag
		//noinspection ConstantConditions
		tag.putString(NBT_SERIALIZER_REGISTRY_NAME_KEY, getRegistryName().toString());
		setVersion(tag, getVersion());
		tag.putString(NBT_SERIALIZER_UUID_KEY, data.getId().toString());

		return tag;
	}

	/**
	 * @param tag     Serialized FlowData
	 * @param version Schema version
	 */
	public static void setVersion(CompoundNBT tag, int version) {
		tag.putInt(NBT_SERIALIZER_SCHEMA_VERSION_KEY, version);
	}

	public abstract T fromBuffer(PacketBuffer buf);

	public abstract void toBuffer(T data, PacketBuffer buf);

}
