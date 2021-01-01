package ca.teamdman.sfm.client.gui.flow.core;

public interface IHasController<T extends IFlowController> {
	T getController();
}
