/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.core;

import java.util.Set;

public interface SelectionHolder<T> {
	Set<T> getSelected();
	void setSelected(T key, boolean value);

	Class<T> getSelectionType();
}
