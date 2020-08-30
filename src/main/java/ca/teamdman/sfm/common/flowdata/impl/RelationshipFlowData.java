package ca.teamdman.sfm.common.flowdata.impl;

import ca.teamdman.sfm.common.flowdata.core.FlowData;
import ca.teamdman.sfm.common.flowdata.core.FlowDataFactory;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class RelationshipFlowData extends FlowData {
	public UUID from, to;

	public RelationshipFlowData(UUID uuid, UUID from, UUID to) {
		super(uuid);
		this.from = from;
		this.to = to;
	}

	public RelationshipFlowData(CompoundNBT tag) {
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
		return new RelationshipFlowData(getId(), from, to);
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

	public static class FlowRelationshipDataFactory extends FlowDataFactory<RelationshipFlowData> {

		public FlowRelationshipDataFactory(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public RelationshipFlowData fromNBT(CompoundNBT tag) {
			return new RelationshipFlowData(tag);
		}
	}
}
