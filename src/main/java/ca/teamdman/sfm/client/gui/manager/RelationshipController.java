package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.ITangible;
import ca.teamdman.sfm.client.gui.impl.FlowRelationship;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateRelationshipPacketC2S;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;

@SuppressWarnings("UnstableApiUsage")
public class RelationshipController implements IFlowController, IFlowView {

	public final ManagerFlowController CONTROLLER;
	private final Position fromPos = new Position();
	private final Position toPos = new Position();
	public MutableGraph<UUID> graph;
	private UUID from;
	private boolean isDragging = false;

	public RelationshipController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	public Stream<RelationshipFlowData> getFlowRelationshipDatas() {
		return CONTROLLER.SCREEN.DATAS.values().stream()
			.filter(data -> data instanceof RelationshipFlowData)
			.map(data -> ((RelationshipFlowData) data));
	}

	public Stream<FlowRelationship> getFlowRelationships() {
		return CONTROLLER.getControllers()
			.filter(c -> c instanceof FlowRelationship)
			.map(c -> ((FlowRelationship) c));
	}

	public void rebuildGraph() {
		graph = GraphBuilder
			.directed()
			.allowsSelfLoops(false)
			.build();
		CONTROLLER.SCREEN.DATAS.values().stream()
			.filter(data -> data instanceof RelationshipFlowData)
			.map(data -> (RelationshipFlowData) data)
			.forEach(data -> {
				graph.addNode(data.from);
				graph.addNode(data.to);
				try {
					graph.putEdge(data.from, data.to);
				} catch (IllegalArgumentException e) {
					SFM.LOGGER.warn(SFMUtil.getMarker(getClass()), "Illegal edge between {} and {}",
						data.from, data.to);
				}
			});
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
		if (!isDragging) {
			return false;
		}
		getElementUnderMouse(mx, my)
			.map(IFlowController::getData)
			.map(Optional::get)
			.map(FlowData::getId)
			.ifPresent(to -> createRelationship(from, to));
		isDragging = false;
		from = null;
		return true;
	}

	public Stream<UUID> getAncestors(UUID child) {
		return SFMUtil.getRecursiveStream((current, enqueue) ->
			graph.predecessors(current).forEach(enqueue), uuid -> true, child);
	}


	public void createRelationship(UUID from, UUID to) {
		if (Objects.equals(from, to)) {
			return;
		}
		graph.addNode(from);
		graph.addNode(to);
		if (getAncestors(from).anyMatch(to::equals)) {
			return;
		}

		PacketHandler.INSTANCE.sendToServer(new ManagerCreateRelationshipPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			UUID.randomUUID(),
			from,
			to
		));
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
		if (isDragging) {
			screen.drawArrow(matrixStack, fromPos, toPos, FlowRelationship.COLOUR);
		}
	}
}
