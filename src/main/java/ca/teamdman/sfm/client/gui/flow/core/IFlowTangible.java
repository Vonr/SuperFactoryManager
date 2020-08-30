package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.common.flowdata.core.Position;
import ca.teamdman.sfm.common.flowdata.core.PositionProvider;
import ca.teamdman.sfm.common.flowdata.core.SizeProvider;
import net.minecraft.util.math.MathHelper;

public interface IFlowTangible extends PositionProvider, SizeProvider {

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
