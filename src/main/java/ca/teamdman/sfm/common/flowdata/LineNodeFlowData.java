package ca.teamdman.sfm.common.flowdata;

import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class LineNodeFlowData extends FlowData implements PositionProvider {

	public Position position;

	public LineNodeFlowData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	public LineNodeFlowData(Position position) {
		this.position = position;
	}

	public LineNodeFlowData() {
		this(new Position(0, 0));
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		FlowDataFactories.LINE_NODE.stampNBT(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
	}

	@Override
	public FlowData copy() {
		return new LineNodeFlowData(getId(), getPosition());
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class LineNodeFlowDataFactory extends FlowDataFactory<LineNodeFlowData> {

		public LineNodeFlowDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public LineNodeFlowData fromNBT(CompoundNBT tag) {
			LineNodeFlowData data = new LineNodeFlowData();
			data.deserializeNBT(tag);
			return data;
		}
	}
}
