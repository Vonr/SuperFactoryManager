package ca.teamdman.sfm.client.gui.flow.impl.manager.template;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import net.minecraft.client.resources.I18n;

public class FlowToolbox extends FlowContainer {

	private final ManagerFlowController CONTROLLER;
	private final FlowDrawer DRAWER;
	private String title;

	public FlowToolbox(ManagerFlowController controller) {
		super(new Position(0, 0), new Size(100, 30));
		CONTROLLER = controller;

		title = I18n.format("gui.sfm.toolbox.title.default");

		DRAWER = new FlowDrawer(new Position(0, 30), 1, 10);
		addChild(DRAWER);

		setDraggable(true);
		setChildrenToDefault();
	}

	public void setChildrenToDefault() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(new InputSpawnerFlowButton(CONTROLLER));
		DRAWER.addChild(new OutputSpawnerFlowButton(CONTROLLER));
		DRAWER.addChild(new FlowTimerTriggerSpawnerButton(CONTROLLER));
		DRAWER.setMaxItemsPerRow(4);
		DRAWER.setMaxItemsPerColumn(1);
		DRAWER.update();
	}

	public void setDrawerChildren(Collection<FlowComponent> children) {
		DRAWER.getChildren().clear();
		children.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.PANEL_BACKGROUND_NORMAL
		);
		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			1,
			CONST.PANEL_BACKGROUND_DARK
		);
		screen.drawString(
			matrixStack,
			title,
			getPosition().getX() + 5,
			getPosition().getY() + 5,
			CONST.TEXT_LIGHT
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 250;
	}
}
