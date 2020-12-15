/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.flow.data.core.SizeHolder;
import net.minecraft.util.math.MathHelper;

public interface IFlowTangible extends PositionHolder, SizeHolder {

	default boolean isInBounds(int mx, int my) {
		return getSize().contains(getPosition(), mx, my);
	}

	default Position getCentroid() {
		return getPosition();
	}

	default Position snapToEdge(Position outside) {
		return new Position(
			MathHelper.clamp(outside.getX(), getPosition().getX(),
				getPosition().getX() + getSize().getWidth()),
			MathHelper.clamp(outside.getY(), getPosition().getY(),
				getPosition().getY() + getSize().getHeight())
		);
	}
}
