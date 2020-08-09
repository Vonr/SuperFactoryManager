package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.common.flowdata.InputData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class FlowInputButton extends FlowIconButton {
	public InputData data;
	public FlowInputButton(InputData data) {
		super(ButtonLabel.INPUT);
		this.data = data;
	}

	public FlowInputButton() {
		this(new InputData());
	}

	@Override
	public void onPositionChanged() {
//				PacketHandler.INSTANCE.sendToServer(new ButtonPositionPacketC2S(
//					CONTAINER.windowId,
//					CONTAINER.getSource().getPos(),
//					0,
//					this.getPosition().getX(),
//					this.getPosition().getY()));
	}
}
