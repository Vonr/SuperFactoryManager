package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.slot.CraftingOutputSlot;
import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.items.SlotItemHandler;

public class WorkstationContainer extends BaseContainer<WorkstationTileEntity> {

	private final PlayerInventory PLAYER_INVENTORY;

	public WorkstationContainer(
		int windowId,
		WorkstationTileEntity tile,
		PlayerInventory playerInv,
		boolean isRemote,
		String data
	) {
		super(SFMContainers.WORKSTATION.get(), windowId, tile, isRemote);
		this.PLAYER_INVENTORY = playerInv;
//		MinecraftForge.EVENT_BUS.register(this);
		SFM.LOGGER.debug(
			"Created container REMOTE={} data={}",
			this.IS_REMOTE,
			data
		);

		//todo: copy events and click logic from craftingresultslot, maybe extend?
		this.addSlot(new CraftingOutputSlot(
			tile.CRAFTING_INVENTORY,
			9,
			118,
			35
		));
		/*
		 * todo: add button for "auto learning"
		 * todo: add text underneath contract item when already learnt; "known"
		 */
		this.addSlot(new CraftingOutputSlot(
			tile.CONTRACT_OUTPUT_INVENTORY,
			0,
			148,
			35
		));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(new SlotItemHandler(
					tile.CRAFTING_INVENTORY,
					j + i * 3,
					30 + j * 18,
					17 + i * 18
				));
			}
		}
		int rows = 9;
		int cols = 5;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				this.addSlot(new SlotItemHandler(
					tile.CONTRACT_INVENTORY,
					col + row * cols,
					-96 + col * 18,
					12 + row * 18
				));
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(
					playerInv,
					j + i * 9 + 9,
					8 + j * 18,
					84 + i * 18
				));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		Slot slot = inventorySlots.get(index);
		if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
		ItemStack slotStack = slot.getStack();
		ItemStack rtn = slotStack.copy();
		if (index == 0) { // crafting output
			if (!mergeItemStack(slotStack, 56, 91+1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (index == 1) { // contract output
			if (!mergeItemStack(slotStack, 56, 91+1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (index >= 2 && index <= 10) { // crafting grid
			if (!mergeItemStack(slotStack, 56, 91+1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (index >= 11 && index <= 55) { // contract inv
			if (!mergeItemStack(slotStack, 56, 91+1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (index >= 56 && index <= 91) { // player inventory
			if (slotStack.getItem() instanceof CraftingContractItem) {
				if (!mergeItemStack(slotStack, 11, 55+1, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (!mergeItemStack(slotStack, 2, 10+1, false)) {
					return ItemStack.EMPTY;
				}
			}
		}
		if (slotStack.isEmpty()) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		return rtn;
	}

	public Stream<ICraftingRecipe> getOneStepRecipes() {
		Set<ItemStack> availableIngredients = IntStream.range(
			0,
			PLAYER_INVENTORY.getSizeInventory()
		)
			.mapToObj(PLAYER_INVENTORY::getStackInSlot)
			.collect(Collectors.toSet());

		return getSource().getWorld().getRecipeManager().getRecipes().stream()
			.filter(ICraftingRecipe.class::isInstance)
			.map(ICraftingRecipe.class::cast)
			.filter(recipe -> isRecipeSatisfied(availableIngredients, recipe));

	}

	public boolean isRecipeSatisfied(
		Set<ItemStack> availableIngredients,
		ICraftingRecipe recipe
	) {
		if (recipe.getIngredients().size() == 0) return false;
		boolean rtn = recipe.getIngredients().stream()
			.allMatch(ingredient ->
				availableIngredients.stream().anyMatch(ingredient)
			);
		return rtn;
	}
}
