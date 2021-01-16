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

public class SelectableFlowButton extends FlowButton {

	protected final String TEXT;
	private boolean selected = false;

	public SelectableFlowButton(
		Position pos,
		Size size,
		String text
	) {
		super(pos, size);
		this.TEXT = text;
	}

	public boolean isSelected() {
		return selected;
	}

	protected void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		setSelected(!isSelected());
		onSelectionChanged();
	}

	public void onSelectionChanged() {

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
}
