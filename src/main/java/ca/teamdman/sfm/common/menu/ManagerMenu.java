package ca.teamdman.sfm.common.menu;

import ca.teamdman.sfm.common.registry.SFMMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ManagerMenu extends AbstractContainerMenu {
    private final Container CONTAINER;
    private final Inventory INVENTORY;

    public ManagerMenu(int windowId, Inventory inv, Container container) {
        super(SFMMenus.MANAGER_MENU.get(), windowId);
        CONTAINER = container;
        INVENTORY = inv;
    }

    public ManagerMenu(int windowId, Inventory inventory, FriendlyByteBuf buf) {
        this(windowId, inventory, new SimpleContainer(0));
    }

    @Override
    public boolean stillValid(Player player) {
        return CONTAINER.stillValid(player);
    }
}
