package ca.teamdman.sfm.common.flowdata;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.Position;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class InputData extends FlowData implements IHasPosition {

	public Position position;

	public InputData() {
		this.uuid = UUID.randomUUID();
		this.position = new Position(0, 0);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
	}

	@Override
	public FlowData copy() {
		InputData copy = new InputData();
		copy.uuid = uuid;
		copy.position = position.copy();
		return copy;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class InputDataFactory extends FlowDataFactory {

		public final ResourceLocation TYPE = new ResourceLocation(SFM.MOD_ID, "input");

		public InputDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public InputData fromNBT(CompoundNBT tag) {
			InputData data = new InputData();
			data.deserializeNBT(tag);
			return data;
		}
	}
}
