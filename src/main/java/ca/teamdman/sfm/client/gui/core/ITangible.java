package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.PositionProvider;
import ca.teamdman.sfm.common.flowdata.SizeProvider;
import net.minecraft.util.math.MathHelper;

public interface ITangible extends PositionProvider, SizeProvider {

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
