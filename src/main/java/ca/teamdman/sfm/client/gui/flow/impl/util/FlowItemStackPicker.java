/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.common.flow.core.Position;
import net.minecraft.item.ItemStack;

public class FlowItemStackPicker extends FlowItemStack {

	public FlowItemStackPicker(
		ItemStack stack,
		Position pos
	) {
		super(stack, pos);
	}

	public void onItemStackChanged() {

	}
}
