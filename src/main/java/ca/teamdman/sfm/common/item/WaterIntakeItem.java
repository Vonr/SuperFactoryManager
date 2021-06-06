package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.registrar.SFMBlocks;
import ca.teamdman.sfm.common.registrar.SFMItems;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class WaterIntakeItem extends BlockItem {

	public WaterIntakeItem() {
		super(
			SFMBlocks.WATER_INTAKE.get(),
			new Item.Properties().group(SFMItems.GROUP)
		);
	}

	@Override
	public void addInformation(
		ItemStack stack,
		@Nullable World worldIn,
		List<ITextComponent> tooltip,
		ITooltipFlag flagIn
	) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent(
			"gui.sfm.tooltip.water_intake.1").mergeStyle(TextFormatting.GRAY));
		tooltip.add(new TranslationTextComponent(
			"gui.sfm.tooltip.water_intake.2").mergeStyle(TextFormatting.GRAY));
		tooltip.add(new TranslationTextComponent(
			"gui.sfm.tooltip.water_intake.3").mergeStyle(TextFormatting.GRAY));
	}
}
