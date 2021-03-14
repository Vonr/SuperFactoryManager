package ca.teamdman.sfm.common.flow.core;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface ItemMatcher {

	boolean matches(@Nonnull ItemStack stack);

	int getQuantity();

	List<ItemStack> getPreview();

	default String getDisplayQuantity() {
		return getQuantity() == 0 || getQuantity() == Integer.MAX_VALUE
			? "\u221E" // ∞ \u221E
			: Integer.toString(getQuantity());
	}


	/**
	 * @return the name of the matcher
	 */
	String getMatcherDisplayName();
}
