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
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.core.Position;
import java.util.Objects;
import java.util.stream.Collectors;

public class ManagerFlowController extends FlowContainer {

	public final ManagerScreen SCREEN;

	public ManagerFlowController(ManagerScreen screen) {
		this.SCREEN = screen;
		rebuildChildren();
		SearchUtil.buildCacheInBackground();
	}

	public void rebuildChildren() {
		getChildren().clear();
		addChild(new FlowBackground());
		addChild(new FlowInstructions(new Position(506,212)));
		addChild(new DebugController(this));
		addChild(new CloneController(this));
		addChild(new DeletionController(this));
		addChild(new RelationshipController(this));
		addChild(new SettingsFlowButton(SCREEN));
		addChild(new InputSpawnerFlowButton(this));
		addChild(new OutputSpawnerFlowButton(this));
		addChild(new FlowTimerTriggerSpawnerButton(this));
		SCREEN.getData()
			.map(data -> data.createController(this))
			.filter(Objects::nonNull)
			.forEach(this::addChild);
	}

	public void notifyDataAdded(FlowData data) {
		addChild(data.createController(this));
	}

	public void notifyDataDeleted(FlowData data) {
		getChildren().stream()
			.filter(c -> c instanceof FlowDataHolder)
			.filter(c -> ((FlowDataHolder) c).getData().equals(data))
			.collect(Collectors.toList())
			.forEach(this::removeChild);
	}
}
