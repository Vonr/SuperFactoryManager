package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.common.flow.core.ItemMatcher;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData.FilterMode;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class ExecutionState {

	private final Map<ItemMatcher, Integer> USAGE_HISTORY = new Object2IntOpenHashMap<>();

	public int getRemainingQuantity(
		ItemMovementRuleFlowData rule,
		@Nullable ItemMatcher matcher
	) {
		if (rule.filterMode == FilterMode.WHITELIST) {
			if (matcher == null) {
				// whitelist mode, no matcher, no transfer allowed
				return 0;
			} else {
				// whitelist mode, matcher found, ensure not over-transferring
				int maxAllowed = matcher.getQuantity();
				int used = USAGE_HISTORY.getOrDefault(matcher, 0);
				return Math.max(0, maxAllowed - used);
			}
		} else if (rule.filterMode == FilterMode.BLACKLIST) {
			// blacklist mode, only transfer if no matcher
			return matcher == null ? Integer.MAX_VALUE : 0;
		}
		return 0;
	}

	public void recordUsage(@Nullable ItemMatcher matcher, int amount) {
		if (matcher != null) {
			USAGE_HISTORY.merge(matcher, amount, Integer::sum);
		}
	}
}
