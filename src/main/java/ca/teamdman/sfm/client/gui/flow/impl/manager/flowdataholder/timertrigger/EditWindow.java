package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.timertrigger;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Objects;
import net.minecraft.client.resources.I18n;

class EditWindow extends FlowContainer {

	private final TimerTriggerFlowComponent PARENT;
	private final TextAreaFlowComponent INPUT;

	public EditWindow(TimerTriggerFlowComponent parent) {
		super(
			parent.BUTTON.getPosition().withConstantOffset(25,0),
			new Size(118, 45)
		);

		addChild(new SectionHeader(
			new Position(5,5),
			new Size(105,15),
			I18n.get("gui.sfm.flow.timer_trigger.label")
		));

		this.INPUT = new TextAreaFlowComponent(
			parent.CONTROLLER.SCREEN,
			Integer.toString(parent.data.interval),
			"#",
			new Position(5, 25),
			new Size(45, 15)
		);
		addChild(INPUT);
		INPUT.setValidator(next -> Objects.nonNull(next) && next.matches("\\d*"));
		INPUT.setResponder(next -> {
			try {
				int nextVal = Integer.parseInt(next);
				if (nextVal != parent.data.interval && nextVal >= 20) {
					// todo: add warning about minimum interval being 20
					// todo: add server config for minimum manager tick interval
					parent.data.interval = nextVal;
					parent.CONTROLLER.SCREEN.sendFlowDataToServer(parent.data);
				}
			} catch (NumberFormatException ignored) {
			}
		});

		this.PARENT = parent;
	}

	public void onDataChanged() {
		INPUT.setContent(Integer.toString(PARENT.data.interval));
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
			I18n.get("gui.sfm.flow.timer_trigger.ticks"),
			getPosition().getX() + INPUT.getPosition().getX() + INPUT.getSize().getWidth() + 5,
			getPosition().getY() + INPUT.getPosition().getY() + 3,
			1,
			CONST.TEXT_LIGHT
		);

		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public boolean isVisible() {
		return PARENT.data.open;
	}

	@Override
	public boolean isEnabled() {
		return isVisible();
	}
}
