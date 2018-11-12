package vswe.stevesfactory.blocks;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

public interface ITileEntityInterface {
	Container getContainer(TileEntity te, InventoryPlayer inv);

	@SideOnly(Side.CLIENT)
	GuiScreen getGui(TileEntity te, InventoryPlayer inv);

	void readAllData(DataReader dr, EntityPlayer player);

	void readUpdatedData(DataReader dr, EntityPlayer player);

	void writeAllData(DataWriter dw);
}
