/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.SearchUtil;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.FlowInstructions;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.FlowTimerTriggerSpawnerButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.InputSpawnerFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.OutputSpawnerFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.SettingsFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBackground;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerClosedEvent;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public class ManagerFlowController extends FlowContainer implements Observer {

	public final ManagerScreen SCREEN;

	public ManagerFlowController(ManagerScreen screen) {
		this.SCREEN = screen;
		rebuildChildren();
		SearchUtil.buildCacheInBackground();
		screen.getFlowDataContainer().addObserver(this);
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
		addChild(new InputSpawnerFlowButton(this));
		addChild(new OutputSpawnerFlowButton(this));
		addChild(new FlowTimerTriggerSpawnerButton(this));

		getDataSortedByDependencies()
			.map(data -> data.createController(this))
			.filter(Objects::nonNull)
			.forEach(this::addChild);
	}

	/**
	 * Uses Kahn's algorithm to do a topological sort Used to ensure that data controllers are built
	 * in proper order during first load of GUI
	 *
	 * https://en.wikipedia.org/wiki/Topological_sorting#Kahn's_algorithm
	 *
	 * @return Sorted FlowData list
	 */
	private Stream<FlowData> getDataSortedByDependencies() {
		Builder<FlowData> result = Stream.builder();

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
		} else if (arg instanceof FlowDataContainerClosedEvent) {
			o.deleteObserver(this);
		}
	}
}
