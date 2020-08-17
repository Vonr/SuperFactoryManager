package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.client.gui.core.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.ITangible;
import ca.teamdman.sfm.client.gui.impl.FlowInputButton;
import ca.teamdman.sfm.client.gui.impl.FlowLineNode;
import ca.teamdman.sfm.client.gui.impl.FlowRelationship;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.InputFlowData;
import ca.teamdman.sfm.common.flowdata.LineNodeFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class ManagerFlowController implements IFlowController, IFlowView {

	public final ManagerScreen SCREEN;
	public final RelationshipController RELATIONSHIP_CONTROLLER = new RelationshipController(this);
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
			Stream.of(RELATIONSHIP_CONTROLLER, CREATE_INPUT_BUTTON),
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
	public void loadFromScreenData() {
		CONTROLLERS.clear();
		SCREEN.DATAS.values().forEach(this::attemptAddDataController);
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
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getControllers()
			.anyMatch(controller -> controller.keyPressed(keyCode, scanCode, modifiers))) {
			return true;
		}
		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			if (getElementUnderMouse(SCREEN.getLatestMouseX(), SCREEN.getLatestMouseY())
				.flatMap(IFlowController::getData)
				.map(FlowData::getId)
				.map(id -> {
					PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
						SCREEN.CONTAINER.windowId,
						SCREEN.CONTAINER.getSource().getPos(),
						id
					));
					return Void.class;
				}).isPresent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return getControllers()
			.anyMatch(controller -> controller.keyReleased(keyCode, scanCode, modifiers));
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime
	) {
		getControllers()
			.map(IFlowController::getView)
			.sorted(Comparator.comparingInt(IFlowView::getZIndex))
			.forEach(view -> view.draw(screen, matrixStack, mx, my, deltaTime));

		if (Screen.hasControlDown() && Screen.hasAltDown()) {
			Optional<FlowData> check =
				RELATIONSHIP_CONTROLLER.CONTROLLER.getElementUnderMouse(mx, my)
					.flatMap(IFlowController::getData);
			check.ifPresent(data -> drawId(screen, matrixStack, data.getId(), mx, my));
			if (!check.isPresent()) {
				RELATIONSHIP_CONTROLLER.getFlowRelationships()
					.filter(r -> r.isCloseTo(mx, my))
					.findFirst()
					.ifPresent(rel -> {
						drawId(screen, matrixStack, rel.data.getId(), mx, my);
						rel.draw(screen, matrixStack, Colour3f.HIGHLIGHT);
					});
			}
		}
	}

	public void drawId(BaseScreen screen, MatrixStack matrixStack, UUID id, int x, int y) {
		String toDraw = id.toString();
		int width = screen.getFontRenderer().getStringWidth(toDraw) + 2;
		int yOffset = -25;
		screen.drawRect(matrixStack, x - 1, y + yOffset - 1, width, 11, Colour3f.WHITE);
		screen.drawString(matrixStack, toDraw, x, y + yOffset, 0x2222BB);
	}

	public Optional<IFlowController> getElementUnderMouse(int mx, int my) {
		return getControllers()
			.filter(e -> e instanceof ITangible)
			.filter(e -> ((ITangible) e).isInBounds(mx, my))
			.filter(e -> e.getData().isPresent())
			.findFirst();
	}
}
