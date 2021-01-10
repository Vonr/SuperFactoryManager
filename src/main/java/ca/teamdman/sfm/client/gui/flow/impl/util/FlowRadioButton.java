package ca.teamdman.sfm.client.gui.flow.impl.util;

import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BACKGROUND_NORMAL;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BACKGROUND_NORMAL_HOVER;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BACKGROUND_SELECTED;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BACKGROUND_SELECTED_HOVER;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BORDER_NORMAL;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BORDER_NORMAL_HOVER;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BORDER_SELECTED;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_BORDER_SELECTED_HOVER;
import static ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST.BUTTON_TEXT_NORMAL;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.HashSet;
import java.util.Optional;

public class FlowRadioButton extends FlowButton {

	private final RadioGroup GROUP;
	private final String TEXT;
	private boolean selected = false;

	public FlowRadioButton(
		Position pos,
		Size size,
		String text,
		RadioGroup group
	) {
		super(pos, size);
		group.addMember(this);
		this.TEXT = text;
		this.GROUP = group;
	}

	public boolean isSelected() {
		return selected;
	}

	protected void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		GROUP.setSelected(this);
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			isSelected()
				? (isHovering() ? BUTTON_BACKGROUND_SELECTED_HOVER : BUTTON_BACKGROUND_SELECTED)
				: (isHovering() ? BUTTON_BACKGROUND_NORMAL_HOVER : BUTTON_BACKGROUND_NORMAL)
		);

		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			1,
			isSelected()
				? (isHovering() ? BUTTON_BORDER_SELECTED_HOVER : BUTTON_BORDER_SELECTED)
				: (isHovering() ? BUTTON_BORDER_NORMAL_HOVER : BUTTON_BORDER_NORMAL)
		);

		screen.drawCenteredString(
			matrixStack,
			TEXT,
			this,
			0.7f,
			BUTTON_TEXT_NORMAL
		);
	}

	/**
	 * Only one member can be selected at once. Members store their selected property.
	 */
	public static class RadioGroup {

		private final HashSet<FlowRadioButton> MEMBERS = new HashSet<>();

		public void addMember(FlowRadioButton member) {
			MEMBERS.add(member);
		}

		public Optional<FlowRadioButton> getSelected() {
			return MEMBERS.stream().filter(FlowRadioButton::isSelected).findFirst();
		}

		public void setSelected(FlowRadioButton member) {
			for (FlowRadioButton button : MEMBERS) {
				button.setSelected(member.equals(button));
			}
			onSelectionChanged(member);
		}

		public void onSelectionChanged(FlowRadioButton member) {

		}
	}
}
