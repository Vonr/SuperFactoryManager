package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowRelationship;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowRelationshipData extends FlowData {
	public UUID from, to;

	public FlowRelationshipData(UUID uuid, UUID from, UUID to) {
		super(uuid);
		this.from = from;
		this.to = to;
	}

	public FlowRelationshipData(CompoundNBT tag) {
		this(null, null, null);
		deserializeNBT(tag);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.putString("from", from.toString());
		tag.putString("to", to.toString());
		FlowDataFactories.RELATIONSHIP.stampNBT(tag);
		return tag;
	}

	@Override
	public FlowData copy() {
		return new FlowRelationshipData(getId(), from, to);
	}


	public boolean matches(UUID from, UUID to) {
		return from.equals(this.from) && to.equals(this.to);
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.from = UUID.fromString(tag.getString("from"));
		this.to = UUID.fromString(tag.getString("to"));
	}

	@Override
	public IFlowController createController(
		IFlowController parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowRelationship((ManagerFlowController) parent, this);
	}

	public static class FlowRelationshipDataFactory extends FlowDataFactory<FlowRelationshipData> {

		public FlowRelationshipDataFactory(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public FlowRelationshipData fromNBT(CompoundNBT tag) {
			return new FlowRelationshipData(tag);
		}
	}
}
