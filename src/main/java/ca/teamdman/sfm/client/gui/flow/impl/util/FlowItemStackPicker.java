/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.SearchUtil;
import ca.teamdman.sfm.client.SearchUtil.Query;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.Collection;
import java.util.Queue;
import net.minecraft.item.ItemStack;

public class FlowItemStackPicker extends FlowContainer {

	private final FlowDrawer DRAWER;
	private final FlowTextArea SEARCH_TEXT_INPUT;
	private final MyQuery QUERY;

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

		this.QUERY = new MyQuery();
		SEARCH_TEXT_INPUT.setResponder(QUERY::updateQuery);
	}

	private void buildPlaceholderItemList() {
		SearchUtil.getSearchableItems().stream()
			.map(stack -> new FlowItemStack(stack, new Position()))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	@Override
	public void tick() {
		if (QUERY.checkResultsAvailable()) {
			DRAWER.getChildren().clear();
			QUERY.getAndSetLatest(null).forEach(stack ->
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

	private class MyQuery extends Query {

		private Collection<ItemStack> latest = null;

		public MyQuery() {
			super("");
		}

		public boolean checkResultsAvailable() {
			return latest != null;
		}

		@Override
		public void onResultsUpdated(Queue<ItemStack> results) {
			getAndSetLatest(results);
		}

		public synchronized Collection<ItemStack> getAndSetLatest(Collection<ItemStack> next) {
			Collection<ItemStack> old = latest;
			latest = next;
			return old;
		}
	}
}
