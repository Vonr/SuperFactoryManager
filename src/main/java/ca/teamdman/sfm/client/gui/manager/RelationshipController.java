package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowRelationship;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.PositionProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;

public class RelationshipController implements IFlowController, IFlowView {

	public final ManagerFlowController CONTROLLER;
	private final Position fromPos = new Position();
	private final Position toPos = new Position();
	private UUID from;
	private boolean isDragging = false;

	public RelationshipController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	public Optional<FlowData> getDataUnderMouse(int mx, int my) {
		return CONTROLLER.getControllers()
			.filter(e -> e.getView().isInBounds(mx, my))
			.map(IFlowController::getData)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(data -> data instanceof PositionProvider)
			.findFirst();
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasShiftDown()) {
			return false;
		}

		Optional<FlowData> data = getDataUnderMouse(mx, my);

		if (data.isPresent()) {
			isDragging = true;
			from = data.get().getId();
			fromPos.setXY(((PositionProvider) data.get()).getCentroid());
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		isDragging = false;
		from = null;
		return false;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		if (!isDragging) {
			return false;
		}
		toPos.setXY(mx, my);
		getDataUnderMouse(mx, my)
			.map(data -> ((PositionProvider) data).getPosition())
			.ifPresent(toPos::setXY);
		return true;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		if (!isDragging) {
			return;
		}
		screen.drawArrow(matrixStack, fromPos, toPos, FlowRelationship.COLOUR);
	}
}
