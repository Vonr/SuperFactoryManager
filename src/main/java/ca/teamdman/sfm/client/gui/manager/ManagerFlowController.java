package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowIconButton;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ButtonPositionPacketC2S;

public class ManagerFlowController implements IFlowController, IFlowView {
	public final ManagerContainer CONTAINER;
	public       FlowIconButton   button = new FlowIconButton(FlowIconButton.ButtonLabel.INPUT) {
		@Override
		public void onPositionChanged() {
			PacketHandler.INSTANCE.sendToServer(new ButtonPositionPacketC2S(
					ManagerFlowController.this.CONTAINER.getSource().getPos(),
					0,
					this.getX(),
					this.getY()));
			ManagerFlowController.this.CONTAINER.getSource().x = this.getX();
		}
	};

	public ManagerFlowController(ManagerContainer container) {
		this.CONTAINER = container;
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
	public void init() {
		this.button.setXY(CONTAINER.x, CONTAINER.y);
	}

	@Override
	public void draw(BaseScreen screen, int mx, int my, float deltaTime) {
		button.draw(screen, mx, my, deltaTime);
	}
}
