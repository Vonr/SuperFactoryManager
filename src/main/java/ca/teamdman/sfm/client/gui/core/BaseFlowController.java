package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.common.container.CoreContainer;

public abstract class BaseFlowController<C extends CoreContainer<?>> implements IFlowController {
	private final C CONTAINER;

	public BaseFlowController(C CONTAINER) {
		this.CONTAINER = CONTAINER;
	}
}
