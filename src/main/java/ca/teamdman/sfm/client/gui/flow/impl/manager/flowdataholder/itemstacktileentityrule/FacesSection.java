package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.SelectableFlowButton;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.Locale;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Direction;

public class FacesSection extends FlowContainer {

	private final ItemRuleFlowComponent PARENT;

	private final FaceFlowButton NORTH;
	private final FaceFlowButton EAST;
	private final FaceFlowButton SOUTH;
	private final FaceFlowButton WEST;
	private final FaceFlowButton UP;
	private final FaceFlowButton DOWN;

	public FacesSection(ItemRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.faces.title")
		));

		NORTH = new FaceFlowButton(new Position(10, 15), new Size(10, 10), Direction.NORTH);
		EAST = new FaceFlowButton(new Position(20, 25), new Size(10, 10), Direction.EAST);
		SOUTH = new FaceFlowButton(new Position(10, 35), new Size(10, 10), Direction.SOUTH);
		WEST = new FaceFlowButton(new Position(0, 25), new Size(10, 10), Direction.WEST);
		UP = new FaceFlowButton(new Position(20, 15), new Size(10, 10), Direction.UP);
		DOWN = new FaceFlowButton(new Position(20, 35), new Size(10, 10), Direction.DOWN);
		addChild(NORTH);
		addChild(EAST);
		addChild(SOUTH);
		addChild(WEST);
		addChild(UP);
		addChild(DOWN);
	}

	public Direction getDirection(FaceFlowButton button) {
		if (button == NORTH) {
			return Direction.NORTH;
		} else if (button == EAST) {
			return Direction.EAST;
		} else if (button == SOUTH) {
			return Direction.SOUTH;
		} else if (button == WEST) {
			return Direction.WEST;
		} else if (button == UP) {
			return Direction.UP;
		} else if (button == DOWN) {
			return Direction.DOWN;
		}
		return null;
	}

	public FaceFlowButton getButton(Direction dir) {
		if (dir == Direction.NORTH) {
			return NORTH;
		} else if (dir == Direction.EAST) {
			return EAST;
		} else if (dir == Direction.SOUTH) {
			return SOUTH;
		} else if (dir == Direction.WEST) {
			return WEST;
		} else if (dir == Direction.UP) {
			return UP;
		} else if (dir == Direction.DOWN) {
			return DOWN;
		}
		return null;
	}

	private class FaceFlowButton extends SelectableFlowButton {

		private final Direction DIRECTION;

		public FaceFlowButton(Position pos, Size size, Direction direction) {
			super(pos, size, direction.name().substring(0, 1).toUpperCase(Locale.ROOT));
			this.DIRECTION = direction;
		}

		@Override
		public boolean isSelected() {
			return PARENT.getData().faces.contains(DIRECTION);
		}

		@Override
		protected void setSelected(boolean selected) {
			if (selected) {
				PARENT.getData().faces.add(DIRECTION);
			} else {
				PARENT.getData().faces.remove(DIRECTION);
			}
			PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
		}
	}
}
