/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;

public abstract class FlowButton extends FlowComponent {

	protected boolean clicking = false;

	public FlowButton(Position pos, Size size) {
		super(pos, size);
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (isInBounds(mx, my) && !canStartDrag()) {
			// Disable button clicking when attempting drag or relationship creation
			clicking = true;
			return true;
		}
		return super.mousePressed(mx, my, button);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (clicking) {
			clicking = false;
			if (isInBounds(mx, my)) {
				this.onClicked(mx, my, button);
				return true;
			}
		}
		return super.mouseReleased(mx, my, button);
	}

	public abstract void onClicked(int mx, int my, int button);
}
