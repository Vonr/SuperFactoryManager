/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.common.flow.core.Position;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public abstract class FlowBlockPosPicker extends FlowContainer {

	private final FlowDrawer DRAWER;

	public FlowBlockPosPicker(
		Position pos
	) {
		super(pos);
		this.DRAWER = new FlowDrawer(
			new Position(0, 0),
			5,
			7
		);
		addChild(DRAWER);
	}

	public void setContents(List<BlockPos> list, World world) {
		DRAWER.getChildren().clear();
		list.stream()
			.map(pos -> new Entry(pos, new ItemStack(world.getBlockState(pos).getBlock().asItem())))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	public abstract void onPicked(BlockPos pos);

	@Override
	public int getZIndex() {
		return super.getZIndex() + 50;
	}

	private class Entry extends ItemStackFlowComponent {

		public final BlockPos POS;

		public Entry(BlockPos pos, ItemStack stack) {
			super(stack, new Position());
			this.POS = pos;
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			List<ITextProperties> rtn = new ArrayList<>(super.getTooltip());
			rtn.add(1,
				new StringTextComponent(POS.toString())
					.mergeStyle(TextFormatting.GRAY)
			);
			return rtn;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			onPicked(POS);
		}
	}
}
