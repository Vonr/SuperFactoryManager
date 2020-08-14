package ca.teamdman.sfm.common.flowdata;

import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowInputData extends FlowData implements PositionProvider {

	public Position position;

	public FlowInputData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	public FlowInputData(Position position) {
		this.position = position;
	}

	public FlowInputData() {
		this(new Position(0, 0));
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		FlowDataFactories.INPUT.stampNBT(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
	}

	@Override
	public FlowData copy() {
		FlowInputData copy = new FlowInputData();
		copy.setId(getId());
		copy.position = position.copy();
		return copy;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowInputDataFactory extends FlowDataFactory<FlowInputData> {

		public FlowInputDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public FlowInputData fromNBT(CompoundNBT tag) {
			FlowInputData data = new FlowInputData();
			data.deserializeNBT(tag);
			return data;
		}
	}
}
