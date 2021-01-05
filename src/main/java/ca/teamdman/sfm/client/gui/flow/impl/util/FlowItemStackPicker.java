/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.SearchUtil;
import ca.teamdman.sfm.client.SearchUtil.Query;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.core.Position;

public class FlowItemStackPicker extends FlowContainer {

	private final FlowDrawer DRAWER;
	private final FlowTextArea SEARCH_TEXT_INPUT;
	private final Query QUERY;
	private int lastCount = -1;

	public FlowItemStackPicker(
		ManagerFlowController controller,
		Position pos
	) {
		super(pos);
		this.DRAWER = new FlowDrawer(
			new Position(0, 20),
			5,
			7
		) {
			@Override
			public void update() {
				super.update();
				SEARCH_TEXT_INPUT.getSize().setWidth(getSize().getWidth());
			}
		};
		this.SEARCH_TEXT_INPUT = new FlowTextArea(
			controller.SCREEN,
			"Placeholder text?",
			new Position(0, 0),
			new Size(100, 20)
		);
		buildPlaceholderItemList();
		addChild(DRAWER);
		addChild(SEARCH_TEXT_INPUT);

		this.QUERY = new Query();
		SEARCH_TEXT_INPUT.setResponder(QUERY::start);
	}

	private void buildPlaceholderItemList() {
		SearchUtil.getSearchableItems().stream()
			.map(stack -> new FlowItemStack(stack, new Position()))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	@Override
	public void tick() {
		if (QUERY.getResults().size() != lastCount) {
			lastCount = QUERY.getResults().size();
			DRAWER.getChildren().clear();
			QUERY.getResults().forEach(stack ->
				DRAWER.addChild(new FlowItemStack(
					stack,
					new Position()
				))
			);
			DRAWER.update();
		}
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 50;
	}

}
