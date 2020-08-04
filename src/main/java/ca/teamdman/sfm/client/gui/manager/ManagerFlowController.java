package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowIconButton;
import ca.teamdman.sfm.client.gui.impl.Position;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ButtonPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;

public class ManagerFlowController implements IFlowController, IFlowView {
	public final ManagerContainer CONTAINER;
	public final FlowIconButton   button;


	public ManagerFlowController(ManagerContainer container) {
		this.CONTAINER = container;
		this.button = new FlowIconButton(FlowIconButton.ButtonLabel.INPUT, new Position(CONTAINER.x, CONTAINER.y) {
			@Override
			public void onPositionChanged(int oldX, int oldY, int newX, int newY) {
				PacketHandler.INSTANCE.sendToServer(new ButtonPositionPacketC2S(
						CONTAINER.windowId,
						CONTAINER.getSource().getPos(),
						0,
						newX,
						newY));
			}
		});
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		return this.button.mouseClicked(screen, mx, my, button);
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		return this.button.mouseReleased(screen, mx, my, button);
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		return this.button.mouseDragged(screen, mx, my, button, dmx, dmy);
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime) {
		button.draw(screen, matrixStack, mx, my, deltaTime);
	}
}
