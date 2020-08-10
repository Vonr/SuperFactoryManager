package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.client.gui.core.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.Position;
import ca.teamdman.sfm.client.gui.impl.FlowInputButton;
import ca.teamdman.sfm.common.flowdata.InputData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.UUID;

public class ManagerFlowController implements IFlowController, IFlowView {

	public final ManagerScreen SCREEN;
	private final ArrayList<FlowInputButton> INPUTS = new ArrayList<>();
	private final FlowIconButton createInputButton = new FlowIconButton(ButtonLabel.ADD_INPUT,
		new Position(25, 25)) {
		@Override
		public void onClicked(BaseScreen screen, int mx, int my, int button) {
			PacketHandler.INSTANCE.sendToServer(new ManagerCreateInputPacketC2S(
				SCREEN.CONTAINER.windowId,
				SCREEN.CONTAINER.getSource().getPos(),
				UUID.randomUUID(),
				0,
				0));
		}
	};

	public ManagerFlowController(ManagerScreen screen) {
		this.SCREEN = screen;
	}

	@Override
	public void load() {
		INPUTS.clear();
		SCREEN.DATAS.values().forEach(d -> {
			if (d instanceof InputData) {
				FlowInputButton button = new FlowInputButton(this, ((InputData) d));
				INPUTS.add(button);
			}
		});
	}

	@Override
	public boolean mousePressed(BaseScreen screen, int mx, int my, int button) {
		if (createInputButton.mousePressed(screen, mx, my, button)) {
			return true;
		}
		for (FlowInputButton btn : INPUTS) {
			if (btn.mousePressed(screen, mx, my, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		if (createInputButton.mouseReleased(screen, mx, my, button)) {
			return true;
		}
		for (FlowInputButton btn : INPUTS) {
			if (btn.mouseReleased(screen, mx, my, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		if (createInputButton.mouseDragged(screen, mx, my, button, dmx, dmy)) {
			return true;
		}
		for (FlowInputButton btn : INPUTS) {
			if (btn.mouseDragged(screen, mx, my, button, dmx, dmy)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime) {
		INPUTS.forEach(b -> b.draw(screen, matrixStack, mx, my, deltaTime));
		createInputButton.draw(screen, matrixStack, mx, my, deltaTime);
	}
}
