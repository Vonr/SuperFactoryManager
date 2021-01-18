package ca.teamdman.sfm.common.tile.manager;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;

/**
 * Represents items that have become available during flow execution. For example, an input rule
 * that whitelists stone will only peek and pop stone from the inventory.
 */
public class ItemStackInputCandidate {

	private final Supplier<ItemStack> PEEKER;
	private final Function<Integer, ItemStack> POPPER;

	public ItemStackInputCandidate(
		Supplier<ItemStack> PEEKER,
		Function<Integer, ItemStack> POPPER
	) {
		this.PEEKER = PEEKER;
		this.POPPER = POPPER;
	}

	/**
	 * Preview the items that this candidate can supply
	 *
	 * @return Withdrawable items in a CapabilityItemHandler
	 */
	public ItemStack peek() {
		return PEEKER.get();
	}

	/**
	 * Retrieves items from the candidate inventory
	 *
	 * @return Items after successful withdrawal
	 */
	public ItemStack pop(int amount) {
		return POPPER.apply(amount);
	}

	/**
	 * When forking, ensure other execution chains don't interfere
	 *
	 * @return new instance with separate {@code consumed} state
	 */
	public ItemStackInputCandidate fork() {
		return new ItemStackInputCandidate(PEEKER, POPPER);
	}
}
