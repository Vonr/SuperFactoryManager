package ca.teamdman.sfm.client.gui.flow.core;

public interface ISelectable {

	boolean isSelected();

	void setSelected(boolean value, boolean notify);

	default void toggleSelected(boolean notify) {
		setSelected(!isSelected(), notify);
	}
}
