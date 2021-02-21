/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowToolbox;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.FlowInstructions;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.SettingsFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBackground;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.CursorFlowData;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ToolboxFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerClosedClientEvent;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public class ManagerFlowController extends FlowContainer implements Observer {

	//todo: button to bring all windows inside bounds of the screen
	// prevents someone from dragging the toolbox off screen

	public final ManagerScreen SCREEN;

	public ManagerFlowController(ManagerScreen screen) {
		super(new Position(), new Size(screen.getScaledWidth(), screen.getScaledHeight()));
		this.SCREEN = screen;
		screen.getFlowDataContainer().addObserver(this);
		rebuildChildren();
	}

	public void rebuildChildren() {
		getChildren().clear();
		addChild(new FlowBackground());
		addChild(new FlowInstructions(new Position(506, 212)));
		addChild(new DebugController(this));
		addChild(new CloneController(this));
		addChild(new DeletionController(this));
		addChild(new RelationshipController(this));
		addChild(new SettingsFlowButton(SCREEN));

		getDataSortedByDependencies()
			.map(data -> data.createController(this))
			.filter(Objects::nonNull)
			.forEach(this::addChild);
	}

	/**
	 * Uses Kahn's algorithm to do a topological sort Used to ensure that data controllers are built
	 * in proper order during first load of GUI
	 * <p>
	 * https://en.wikipedia.org/wiki/Topological_sorting#Kahn's_algorithm
	 *
	 * @return Sorted FlowData list
	 */
	private Stream<FlowData> getDataSortedByDependencies() {
		Builder<FlowData> result = Stream.builder();

		// Only classes present in the datalist are relevant.
		// Topological sort just used to ensure things added in correct order.
		// "Missing" dependencies is fine, since they can't be added out of order.
		Set<Class<? extends FlowData>> present = SCREEN.getFlowDataContainer().stream()
			.map(FlowData::getClass)
			.collect(Collectors.toSet());

		HashMap<FlowData, Set<Class<? extends FlowData>>> dependencies =
			SCREEN.getFlowDataContainer().stream()
				.collect(
					HashMap::new,
					(map, data) -> map.put(
						data,
						data.getDependencies().stream()
							.filter(present::contains)
							.collect(Collectors.toSet())
					),
					HashMap::putAll
				);
		dependencies.entrySet().removeIf(entry -> entry.getValue().isEmpty());

		ArrayDeque<FlowData> remaining = SCREEN.getFlowDataContainer().stream()
			.filter(data -> data.getDependencies().stream().noneMatch(present::contains))
			.collect(ArrayDeque::new, ArrayDeque::add, ArrayDeque::addAll);

		while (!remaining.isEmpty()) {
			FlowData n = remaining.pop();
			result.add(n);
			Iterator<Entry<FlowData, Set<Class<? extends FlowData>>>> iter = dependencies
				.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<FlowData, Set<Class<? extends FlowData>>> entry = iter.next();
				entry.getValue().remove(n.getClass());
				if (entry.getValue().size() == 0) {
					remaining.add(entry.getKey());
					iter.remove();
				}
			}
		}

		if (dependencies.size() > 0) {
			throw new IllegalArgumentException("Circular FlowData dependency chain detected");
		}

		return result.build();
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return super.mousePressed(mx, my, button);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (super.mouseReleased(mx, my, button)) {
			return true;
		}

		// reset toolbox when clicking "background"
		if (isInBounds(mx, my)) {
			this.findFirstChild(FlowToolbox.class).ifPresent(FlowToolbox::setChildrenToDefault);
			return true;
		}

		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		if (super.keyReleased(keyCode, scanCode, modifiers, mx, my)) {
			return true;
		}

		// if "inventory" key pressed, and the event wasn't consumed, close gui
		if (
			!Client.preventClosingManagerWithInventoryButton
				&& SCREEN.getMinecraft().gameSettings.keyBindInventory.matchesKey(keyCode, scanCode)
		) {
			SCREEN.closeScreen();
			return true;
		}

		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			if (change.CHANGE == ChangeType.REMOVED) {
				getChildren().stream()
					.filter(FlowDataHolder.class::isInstance)
					.filter(c -> ((FlowDataHolder) c).getData().getId().equals(change.DATA.getId()))
					.collect(Collectors.toList())
					.forEach(this::removeChild);
			} else if (change.CHANGE == ChangeType.ADDED) {
				addChild(change.DATA.createController(this));
			}
		} else if (arg instanceof FlowDataContainerClosedClientEvent) {
			o.deleteObserver(this);
		}
	}

	public void init() {
		createPlayerCursor();
		createToolbox();
	}

	/**
	 * Creates a CursorFlowData to display this client's cursor to other players. This data object
	 * is cleaned up server side when the player closes the container.
	 */
	public void createPlayerCursor() {
		ClientPlayerEntity localPlayer = SCREEN.getMinecraft().player;
		SCREEN.getFlowDataContainer().get(localPlayer.getUniqueID(), CursorFlowData.class)
			.orElseGet(() -> {
				CursorFlowData next = new CursorFlowData(
					localPlayer.getUniqueID(),
					localPlayer.getScoreboardName(),
					new Position()
				);
				SCREEN.sendFlowDataToServer(next);
				return next;
			});
	}

	/**
	 * Create data for the toolbox if none exists
	 */
	public void createToolbox() {
		SCREEN.getFlowDataContainer().get(ToolboxFlowData.class).findAny().orElseGet(() -> {
			ToolboxFlowData data = new ToolboxFlowData(
				UUID.randomUUID(),
				new Position()
			);
			SCREEN.sendFlowDataToServer(data);
			return data;
		});
	}
}
