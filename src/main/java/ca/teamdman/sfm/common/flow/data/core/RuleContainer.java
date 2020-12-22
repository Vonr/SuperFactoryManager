/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.core;

import java.util.List;
import java.util.UUID;

public interface RuleContainer {
	List<UUID> getRules();
	void setRules(List<UUID> rules);
}
