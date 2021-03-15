package ca.teamdman.sfm.common.flow.core;

public interface VisibilityHolder {
	boolean isVisible();
	void setVisibility(boolean open);
	default void toggleVisibility() {
		setVisibility(!isVisible());
	}
}
