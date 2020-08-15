package ca.teamdman.sfm.common.flowdata;

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

	public FlowRelationshipData(UUID from, UUID to) {
		this.from = from;
		this.to = to;
	}

	public FlowRelationshipData(CompoundNBT tag) {
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

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.from = UUID.fromString(tag.getString("from"));
		this.to = UUID.fromString(tag.getString("to"));
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
