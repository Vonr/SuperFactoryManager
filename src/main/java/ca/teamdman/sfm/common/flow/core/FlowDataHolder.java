/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.core;

import ca.teamdman.sfm.common.flow.data.FlowData;

public interface FlowDataHolder<T extends FlowData> {
	T getData();
	void setData(T data);
	default boolean isDeletable() {
		return false;
	}
	default boolean isCloneable() { return false; }
}
