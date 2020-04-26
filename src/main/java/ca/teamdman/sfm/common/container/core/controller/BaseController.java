package ca.teamdman.sfm.common.container.core.controller;

import ca.teamdman.sfm.common.container.CoreContainer;
import net.minecraft.client.gui.IHasContainer;

public abstract class BaseController implements IHasContainer<CoreContainer<?>> {
	protected final CoreContainer<?> CONTAINER;

	public BaseController(CoreContainer<?> CONTAINER) {
		this.CONTAINER = CONTAINER;
	}

	@Override
	public CoreContainer<?> getContainer() {
		return CONTAINER;
	}
}
