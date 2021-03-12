package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.BasicInputSpawnerFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.BasicOutputSpawnerFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.FlowTimerTriggerSpawnerButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ToolboxFlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collection;
import net.minecraft.client.resources.I18n;

public class FlowToolbox extends FlowContainer implements FlowDataHolder<ToolboxFlowData> {

	private final ManagerFlowController CONTROLLER;
	private final FlowDrawer DRAWER;
	private ToolboxFlowData data;
	private String title;

	public FlowToolbox(ManagerFlowController controller, ToolboxFlowData data) {
		super(data.getPosition(), new Size(100, 30));
		this.data = data;
		CONTROLLER = controller;

		title = I18n.format("gui.sfm.toolbox.title.default");

		DRAWER = new FlowDrawer(new Position(0, 30), 1, 10);
		addChild(DRAWER);

		setDraggable(true);
		setChildrenToDefault();
	}

	public void setChildrenToDefault() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(new FlowTimerTriggerSpawnerButton(CONTROLLER));
		DRAWER.addChild(new BasicInputSpawnerFlowButton(CONTROLLER));
		DRAWER.addChild(new BasicOutputSpawnerFlowButton(CONTROLLER));
		DRAWER.setMaxItemsPerRow(4);
		DRAWER.setMaxItemsPerColumn(3);
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
			CONST.PANEL_BORDER
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

	@Override
	public ToolboxFlowData getData() {
		return data;
	}

	@Override
	public void setData(ToolboxFlowData data) {
		this.data = data;
		setPosition(data.getPosition());
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		CONTROLLER.SCREEN.sendFlowDataToServer(data);
	}
}
