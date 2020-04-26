package ca.teamdman.sfm.common.flow.core;

import ca.teamdman.sfm.common.container.core.ISprite;
import ca.teamdman.sfm.common.container.core.Sprite;
import ca.teamdman.sfm.common.container.core.component.Component;
import net.minecraft.util.ResourceLocation;

public class BlankFlowComponent extends FlowComponent {
	public BlankFlowComponent(ResourceLocation key) {
		super(key);
	}

	@Override
	public ISprite getSprite() {
		return Sprite.CASE;
	}

	@Override
	public void onClick(Component c) {

	}

	@Override
	public String getTranslationKey() {
		return "";
	}
}
