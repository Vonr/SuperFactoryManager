package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

class SectionHeader extends FlowComponent {

	private final String CONTENT;

	public SectionHeader(Position pos, Size size, String CONTENT) {
		super(pos, size);
		this.CONTENT = CONTENT;
		setEnabled(false);
	}

	@Override
	public void draw(
		BaseScreen screen,
		MatrixStack matrixStack,
		int mx,
		int my,
		float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.PANEL_BACKGROUND_DARK
		);
		screen.drawCenteredString(
			matrixStack,
			CONTENT,
			this,
			1,
			CONST.TEXT_LIGHT
		);
	}
}
