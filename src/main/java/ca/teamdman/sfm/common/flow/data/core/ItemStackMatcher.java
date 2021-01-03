package ca.teamdman.sfm.common.flow.data.core;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface ItemStackMatcher {

	boolean matches(@Nonnull ItemStack stack);

	int getQuantity();

	Collection<ItemStack> getPreview();
}
