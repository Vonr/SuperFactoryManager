package ca.teamdman.sfm.common.flow.impl;

import ca.teamdman.sfm.common.container.core.ISprite;
import ca.teamdman.sfm.common.container.core.Point;
import ca.teamdman.sfm.common.container.core.Sprite;
import ca.teamdman.sfm.common.container.core.component.Component;
import ca.teamdman.sfm.common.flow.core.FlowComponent;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ItemInputFlowComponent extends FlowComponent {
	private Point position = new Point(0,0);
	public ItemInputFlowComponent(ResourceLocation name) {
		super(name);
	}
//	@Override
//	public void onClick(Component c) {
//		PacketHandler.INSTANCE.sendToServer(new ManagerUpdatePacket("howdy"));
//	}


	@Override
	public CompoundNBT serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}
}
