package ca.teamdman.sfm.common.flowdata.core;

import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class FlowData implements INBTSerializable<CompoundNBT>, ICopyable<FlowData> {

	private UUID uuid;

	public FlowData(UUID uuid) {
		this.uuid = uuid;
	}

	public FlowData() {
		this.uuid = UUID.randomUUID();
	}

	public UUID getId() {
		return uuid;
	}

	public void setId(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("uuid", uuid.toString());
		return tag;
	}

	@Override
	public FlowData copy() {
		FlowData copy = new FlowData();
		copy.uuid = uuid;
		return copy;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.uuid = UUID.fromString(nbt.getString("uuid"));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FlowData && ((FlowData) obj).getId().equals(getId());
	}

	@Override
	public String toString() {
		return getId().toString();
	}
}
