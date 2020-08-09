package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowInputButton;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.InputData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;

public class ManagerFlowController implements IFlowController, IFlowView {

	public final ManagerContainer CONTAINER;
	private final ArrayList<FlowInputButton> INPUTS = new ArrayList<>();

	public ManagerFlowController(ManagerContainer container) {
		this.CONTAINER = container;
		container.DATA.forEach(d -> {
			if (d instanceof InputData) {
				FlowInputButton button = new FlowInputButton(((InputData) d));
				INPUTS.add(button);
			}
		});
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		for (FlowInputButton btn : INPUTS) {
			if (btn.mouseClicked(screen, mx, my, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		for (FlowInputButton btn : INPUTS) {
			if (btn.mouseReleased(screen, mx, my, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
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
	}
}
