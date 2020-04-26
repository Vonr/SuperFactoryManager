package ca.teamdman.sfm.common.flow.impl;

import ca.teamdman.sfm.common.container.core.ISprite;
import ca.teamdman.sfm.common.container.core.Sprite;
import ca.teamdman.sfm.common.container.core.component.Component;
import ca.teamdman.sfm.common.flow.core.FlowComponent;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.util.ResourceLocation;

public class ItemOutputFlowComponent extends FlowComponent {
	public ItemOutputFlowComponent(ResourceLocation name) {
		super(name);
	}

	@Override
	public ISprite getSprite() {
		return Sprite.INPUT;
	}

	@Override
	public void onClick(Component c) {
		PacketHandler.INSTANCE.sendToServer(new ManagerUpdatePacket("howdy"));
	}

	@Override
	public String getTranslationKey() {
		return "out";
	}
}
