/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.SearchUtil.Query;
import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public abstract class FlowItemStackPicker extends FlowContainer {

	private final FlowDrawer DRAWER;
	private final TextAreaFlowComponent SEARCH_TEXT_INPUT;
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
		DRAWER.setShrinkToFit(false);
		this.SEARCH_TEXT_INPUT = new TextAreaFlowComponent(
			controller.SCREEN,
			"",
			I18n.format("gui.sfm.flow.search.placeholder"),
			new Position(0, 0),
			new Size(100, 20)
		);
		addChild(DRAWER);
		addChild(SEARCH_TEXT_INPUT);

		this.QUERY = new Query();
		SEARCH_TEXT_INPUT.setResponder(QUERY::start);
		QUERY.start("");
	}

	@Override
	public void tick() {
		if (QUERY.getResults().size() != lastCount) {
			lastCount = QUERY.getResults().size();
			DRAWER.getChildren().clear();
			if (QUERY.getResults().size() == 0) {
				DRAWER.addChild(new NoResultsFoundLabelFlowComponent());
			} else {
				QUERY.getResults().forEach(stack ->
					DRAWER.addChild(new ItemStackFlowComponent(
						stack,
						new Position()
					) {
						@Override
						public void onClicked(int mx, int my, int button) {
							onItemStackChanged(stack);
						}
					})
				);
			}
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
			super(new Position(), ItemStackFlowComponent.DEFAULT_SIZE);
		}

		@Override
		public void draw(
			BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
		) {
			screen.drawString(
				matrixStack,
				I18n.format("gui.sfm.flow.search.no_results_found"),
				5,
				5,
				CONST.TEXT_LIGHT
			);
		}
	}
}
