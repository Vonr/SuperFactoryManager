package ca.teamdman.sfm.common.flow.core;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface TileMatcher {

	boolean matches(@Nonnull TileEntity tile);

	List<ItemStack> getPreview(CableNetwork network);

	/**
	 * @return the name of the matcher
	 */
	String getMatcherDisplayName();
}
