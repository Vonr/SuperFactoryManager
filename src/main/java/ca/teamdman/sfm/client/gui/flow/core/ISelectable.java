/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

public interface ISelectable {

	boolean isSelected();

	void setSelected(boolean value, boolean notify);

	default void toggleSelected(boolean notify) {
		setSelected(!isSelected(), notify);
	}
}
