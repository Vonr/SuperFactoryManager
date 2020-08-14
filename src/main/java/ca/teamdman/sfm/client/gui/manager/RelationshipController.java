package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.ITangible;
import ca.teamdman.sfm.client.gui.impl.FlowRelationship;
import ca.teamdman.sfm.common.flowdata.Position;
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

	public Optional<IFlowController> getElementUnderMouse(int mx, int my) {
		return CONTROLLER.getControllers()
			.filter(e -> e instanceof ITangible)
			.filter(e -> ((ITangible) e).isInBounds(mx, my))
			.filter(e -> e.getData().isPresent())
			.findFirst();
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasShiftDown()) {
			return false;
		}

		Optional<IFlowController> controller = getElementUnderMouse(mx, my);
		if (controller.isPresent()) {
			isDragging = true;
			//noinspection OptionalGetWithoutIsPresent
			from = controller.get().getData().get().getId();
			fromPos.setXY(((ITangible) controller.get()).getCentroid());
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
		getElementUnderMouse(mx, my)
			.map(x -> ((ITangible) x).snapToEdge(fromPos))
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
