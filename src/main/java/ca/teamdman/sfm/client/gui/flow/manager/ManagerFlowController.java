package ca.teamdman.sfm.client.gui.flow.manager;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.impl.datadriven.FlowInputButton;
import ca.teamdman.sfm.client.gui.flow.impl.datadriven.FlowLineNode;
import ca.teamdman.sfm.client.gui.flow.impl.datadriven.FlowRelationship;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.core.FlowData;
import ca.teamdman.sfm.common.flowdata.core.Position;
import ca.teamdman.sfm.common.flowdata.impl.InputFlowData;
import ca.teamdman.sfm.common.flowdata.impl.LineNodeFlowData;
import ca.teamdman.sfm.common.flowdata.impl.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.lwjgl.glfw.GLFW;

public class ManagerFlowController implements IFlowController, IFlowView {

	public final ManagerScreen SCREEN;
	public final RelationshipController RELATIONSHIP_CONTROLLER = new RelationshipController(this);
	public final DebugController DEBUG_CONTROLLER = new DebugController(this);
	public final CloneController CLONE_CONTROLLER = new CloneController(this);
	private final LinkedHashMap<UUID, IFlowController> CONTROLLERS = new LinkedHashMap<>();
	private final FlowIconButton CREATE_INPUT_BUTTON = new FlowIconButton(
		ButtonLabel.ADD_INPUT,
		new Position(25, 25)
	) {
		@Override
		public void onClicked(int mx, int my, int button) {
			PacketHandler.INSTANCE.sendToServer(new ManagerCreateInputPacketC2S(
				SCREEN.CONTAINER.windowId,
				SCREEN.CONTAINER.getSource().getPos(),
				new Position(0, 0)
			));
		}
	};

	public ManagerFlowController(ManagerScreen screen) {
		this.SCREEN = screen;
	}

	public Stream<IFlowController> getControllers() {
		return Stream.concat(
			Stream.of(
				DEBUG_CONTROLLER,
				CLONE_CONTROLLER,
				RELATIONSHIP_CONTROLLER,
				CREATE_INPUT_BUTTON
			),
			CONTROLLERS.values().stream()
		);
	}

	public Optional<IFlowController> getController(UUID id) {
		return Optional.ofNullable(CONTROLLERS.get(id));
	}

	public void addController(UUID id, IFlowController controller) {
		CONTROLLERS.put(id, controller);
	}

	public Optional<IFlowController> createControllerForDataType(FlowData data) {
		if (data instanceof InputFlowData) {
			return Optional.of(new FlowInputButton(this, ((InputFlowData) data)));
		} else if (data instanceof RelationshipFlowData) {
			return Optional.of(new FlowRelationship(
				this,
				((RelationshipFlowData) data)
			));
		} else if (data instanceof LineNodeFlowData) {
			return Optional.of(new FlowLineNode(this, ((LineNodeFlowData) data)));
		}
		return Optional.empty();
	}

	public void attemptAddDataController(FlowData data) {
		createControllerForDataType(data)
			.ifPresent(c -> addController(data.getId(), c));
	}

	@Override
	public void onDataChange() {
		CONTROLLERS.clear();
		SCREEN.DATAS.values().forEach(this::attemptAddDataController);
	}

	public void onDataChange(UUID dataId) {
		getControllers()
			.filter(c -> c.getData().filter(d -> d.getId().equals(dataId)).isPresent())
			.forEach(IFlowController::onDataChange);
	}


	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return getControllers()
			.anyMatch(controller -> controller.mousePressed(mx, my, button));
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		return getControllers()
			.anyMatch(controller -> controller.mouseReleased(mx, my, button));
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return getControllers()
			.anyMatch(controller -> controller.mouseDragged(mx, my, button, dmx, dmy));
	}

	@Override
	public boolean mouseScrolled(int mx, int my, double scroll) {
		return getControllers()
			.anyMatch(controller -> controller.mouseScrolled(mx, my, scroll));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		if (getControllers()
			.anyMatch(controller -> controller.keyPressed(keyCode, scanCode, modifiers, mx, my))) {
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			return getElementUnderMouse(mx, my)
				.flatMap(IFlowController::getData)
				.map(FlowData::getId)
				.map(id -> {
					PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
						SCREEN.CONTAINER.windowId,
						SCREEN.CONTAINER.getSource().getPos(),
						id
					));
					return Void.class;
				}).isPresent();
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return getControllers()
			.anyMatch(controller -> controller.keyReleased(keyCode, scanCode, modifiers, mx, my));
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		getControllers()
			.map(IFlowController::getView)
			.sorted(Comparator.comparingInt(IFlowView::getZIndex))
			.forEach(view -> view.draw(screen, matrixStack, mx, my, deltaTime));
	}

	public Optional<IFlowController> getElementUnderMouse(int mx, int my) {
		return getControllers()
			.filter(e -> e instanceof IFlowTangible)
			.filter(e -> ((IFlowTangible) e).isInBounds(mx, my))
			.filter(e -> e.getData().isPresent())
			.findFirst();
	}
}
