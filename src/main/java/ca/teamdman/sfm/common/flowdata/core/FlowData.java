package ca.teamdman.sfm.common.flowdata.core;

import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class FlowData implements INBTSerializable<CompoundNBT>, ICopyable<FlowData> {

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
	public abstract FlowData copy();

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

	@OnlyIn(Dist.CLIENT)
	public abstract IFlowController createController(
		IFlowController parent
	);
}
