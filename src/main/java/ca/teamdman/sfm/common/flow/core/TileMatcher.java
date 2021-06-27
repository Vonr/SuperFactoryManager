package ca.teamdman.sfm.common.flow.core;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextProperties;

public interface TileMatcher extends VisibilityHolder {

	boolean matches(@Nonnull TileEntity tile);

	List<ItemStack> getPreview(CableNetwork network);

	List<? extends ITextProperties> getTooltip(List<? extends ITextProperties> normal);
}
