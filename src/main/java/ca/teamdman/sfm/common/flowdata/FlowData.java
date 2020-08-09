package ca.teamdman.sfm.common.flowdata;

import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;

public class FlowData implements IFlowData {

	public UUID uuid;

	@Override
	public UUID getId() {
		return uuid;
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
}
