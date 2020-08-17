package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.common.flowdata.FlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;

public class CloneController implements IFlowController, IFlowView {

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return false;
	}

	@Override
	public IFlowView getView() {
		return null;
	}

	@Override
	public Optional<FlowData> getData() {
		return Optional.empty();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {

	}

	@Override
	public int getZIndex() {
		return 1;
	}
}
