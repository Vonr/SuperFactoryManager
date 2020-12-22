/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.common.flow.data.core.FlowData;
import java.util.UUID;
import net.minecraft.item.ItemStack;

public abstract class FlowRuleData extends FlowData {
	public String name;
	public ItemStack icon;

	public FlowRuleData(UUID uuid, String name, ItemStack icon) {
		super(uuid);
		this.name = name;
		this.icon = icon;
	}


	@Override
	public void merge(FlowData other) {
		if (other instanceof FlowRuleData) {
			name = ((FlowRuleData) other).name;
			icon = ((FlowRuleData) other).icon;
		}
	}
}
