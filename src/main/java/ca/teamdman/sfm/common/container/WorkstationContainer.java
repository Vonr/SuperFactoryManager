package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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

//		this.addSlot(new CraftingOutputSlot(tile.INVENTORY, 9, 124, 35));
//		for (int i = 0; i < 3; i++) {
//			for (int j = 0; j < 3; j++) {
//				this.addSlot(
//					new SlotItemHandler(tile.INVENTORY, j + i * 3, 30 + j * 18, 17 + i * 18));
//			}
//		}

		for (int row = 0; row < 3; row++) {
			for (int slot = 0; slot < 9; slot++) {
				this.addSlot(new SlotItemHandler(
					tile.INVENTORY,
					slot + row * 9,
					-104 +  8 + slot * 18,
					12 + row * 18
				));
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
		}
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


}
