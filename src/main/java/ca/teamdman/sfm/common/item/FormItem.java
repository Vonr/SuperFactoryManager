package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.render.FormItemExtensions;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class FormItem extends Item {
    public FormItem() {
        super(new Item.Properties());
    }

    public static ItemStack getForm(@Nonnull ItemStack stack) {
        var formStack = new ItemStack(SFMItems.FORM_ITEM.get());
        formStack.set(SFMDataComponents.FORM_REFERENCE, stack);
        return formStack;
    }

    public static ItemStack getReference(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.FORM_REFERENCE, ItemStack.EMPTY);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new FormItemExtensions());
    }


    @Override
    public void appendHoverText(
            ItemStack pStack,
            TooltipContext pContext,
            List<Component> pTooltipComponents,
            TooltipFlag pTooltipFlag
    ) {
        var reference = getReference(pStack);
        if (!reference.isEmpty()) {
            pTooltipComponents.add(reference.getHoverName());
            reference.getItem().appendHoverText(reference, pContext, pTooltipComponents, pTooltipFlag);
        }
    }
}
