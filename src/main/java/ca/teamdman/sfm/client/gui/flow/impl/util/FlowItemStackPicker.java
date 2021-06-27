/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.SearchUtil;
import ca.teamdman.sfm.client.SearchUtil.SearchResults;
import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public abstract class FlowItemStackPicker extends FlowContainer {

	private final FlowDrawer DRAWER;
	private final TextAreaFlowComponent SEARCH_TEXT_INPUT;
	private SearchResults searchResults;
	private static final Comparator<FlowComponent> RESULT_COMPARATOR = Comparator.comparing(
		c -> c instanceof ItemStackFlowButton
			? ((ItemStackFlowButton) c).getStack()
			: ItemStack.EMPTY,
		SearchUtil.SEARCH_RESULT_COMPARATOR
	);

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
		DRAWER.setShrinkToFit(false);
		this.SEARCH_TEXT_INPUT = new TextAreaFlowComponent(
			controller.SCREEN,
			"",
			I18n.get("gui.sfm.flow.search.placeholder"),
			new Position(0, 0),
			new Size(100, 20)
		);
		addChild(DRAWER);
		addChild(SEARCH_TEXT_INPUT);

		SEARCH_TEXT_INPUT.setResponder(text -> {
			if (searchResults != null) {
				searchResults.cancel();
			}
			DRAWER.getChildren().clear();
			searchResults = SearchUtil.search(text);
		});
		searchResults = SearchUtil.search("");
	}

	@Override
	public void tick() {
		if (searchResults == null) return;

		int previous = DRAWER.getChildren().size();

		// populate latest search results
		searchResults.streamLatestResults()
			.map(SearchResultStack::new)
			.forEach(DRAWER::addChild);

		// if search finished and no results, display so to user
		if (DRAWER.getChildren().size() == 0 && searchResults.isFinished()) {
			DRAWER.addChild(new NoResultsFoundLabelFlowComponent());
		}

		// update drawer if contents changed
		if (DRAWER.getChildren().size() != previous) {
			DRAWER.getChildren().sort(RESULT_COMPARATOR);
			DRAWER.update();
		}
	}

	public abstract void onItemStackChanged(ItemStack stack);

	@Override
	public int getZIndex() {
		return super.getZIndex() + 50;
	}

	private static class NoResultsFoundLabelFlowComponent extends FlowComponent {
		public NoResultsFoundLabelFlowComponent() {
			super(new Position(), ItemStackFlowButton.DEFAULT_SIZE);
		}

		@Override
		public void draw(
			BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
		) {
			screen.drawString(
				matrixStack,
				I18n.get("gui.sfm.flow.search.no_results_found"),
				5,
				5,
				CONST.TEXT_LIGHT
			);
		}
	}

	private class SearchResultStack extends ItemStackFlowButton {

		private final ItemStack stack;

		public SearchResultStack(ItemStack stack) {
			super(stack, new Position());
			this.stack = stack;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			// hide after picking
			FlowItemStackPicker.this.setVisible(false);
			FlowItemStackPicker.this.setEnabled(false);

			// invoke callback
			onItemStackChanged(stack);
		}
	}
}
