package ca.teamdman.sfm.common.flow.core;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface ItemStackMatcher {

	boolean matches(@Nonnull ItemStack stack);

	int getQuantity();

	List<ItemStack> getPreview();

	default String getDisplayQuantity() {
		return getQuantity() == 0 || getQuantity() == Integer.MAX_VALUE
			? "∞" // ∞
			: Integer.toString(getQuantity());
	}
}
