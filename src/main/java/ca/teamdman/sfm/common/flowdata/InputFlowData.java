package ca.teamdman.sfm.common.flowdata;

import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class InputFlowData extends FlowData implements PositionProvider {

	public Position position;

	public InputFlowData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	public InputFlowData(Position position) {
		this.position = position;
	}

	public InputFlowData() {
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
		return new InputFlowData(getId(), getPosition());
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowInputDataFactory extends FlowDataFactory<InputFlowData> {

		public FlowInputDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public InputFlowData fromNBT(CompoundNBT tag) {
			InputFlowData data = new InputFlowData();
			data.deserializeNBT(tag);
			return data;
		}
	}
}
