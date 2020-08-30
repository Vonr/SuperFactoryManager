package ca.teamdman.sfm.common.flowdata.core;

import java.util.Set;

public interface SelectionHolder<T> {
	Set<T> getSelected();
	void setSelected(T key, boolean value);

	Class<T> getSelectionType();
}
