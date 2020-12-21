/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

public interface IFlowCloneable {
	/**
	 * Fired when the clone should be committed, i.e., send packet to create new flow data here.
	 * !! It is expected that the clone will have a new UUID for the data. !!
	 *
	 * @param x X location for cloned element
	 * @param y Y location for cloned element
	 */
	void cloneWithPosition(int x, int y);
}
