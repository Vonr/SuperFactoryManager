package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.client.gui.core.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowInputButton;
import ca.teamdman.sfm.client.gui.impl.FlowRelationship;
import ca.teamdman.sfm.common.flowdata.InputFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ManagerFlowController implements IFlowController, IFlowView {

	public final ManagerScreen SCREEN;
	private final LinkedHashMap<UUID, IFlowController> CONTROLLERS = new LinkedHashMap<>();
	public final RelationshipController RELATIONSHIP_CONTROLLER = new RelationshipController(this);
	private final FlowIconButton CREATE_INPUT_BUTTON = new FlowIconButton(ButtonLabel.ADD_INPUT,
		new Position(25, 25)) {
		@Override
		public void onClicked(int mx, int my, int button) {
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
		RELATIONSHIP_CONTROLLER.rebuildGraph();
	}

	public Stream<IFlowController> getControllers() {
		return Stream.concat(Stream.of(RELATIONSHIP_CONTROLLER, CREATE_INPUT_BUTTON),
			CONTROLLERS.values().stream());
	}

	public Optional<IFlowController> getController(UUID id) {
		return Optional.ofNullable(CONTROLLERS.get(id));
	}

	@Override
	public void loadFromScreenData() {
		CONTROLLERS.clear();
		SCREEN.DATAS.values().forEach(data -> {
			if (data instanceof InputFlowData) {
				FlowInputButton element = new FlowInputButton(this, ((InputFlowData) data));
				CONTROLLERS.put(data.getId(), element);
			} else if (data instanceof RelationshipFlowData) {
				FlowRelationship element = new FlowRelationship(this,
					((RelationshipFlowData) data));
				CONTROLLERS.put(data.getId(), element);
			}
		});
		RELATIONSHIP_CONTROLLER.rebuildGraph();
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		for (Iterator<IFlowController> it = getControllers().iterator(); it.hasNext(); ) {
			IFlowController btn = it.next();
			if (btn.mousePressed(mx, my, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		for (Iterator<IFlowController> it = getControllers().iterator(); it.hasNext(); ) {
			IFlowController btn = it.next();
			if (btn.mouseReleased(mx, my, button)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		for (Iterator<IFlowController> it = getControllers().iterator(); it.hasNext(); ) {
			IFlowController btn = it.next();
			if (btn.mouseDragged(mx, my, button, dmx, dmy)) {
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
		getControllers()
			.map(IFlowController::getView)
			.sorted(Comparator.comparingInt(IFlowView::getZIndex))
			.forEach(view -> view.draw(screen, matrixStack, mx, my, deltaTime));
	}
}
