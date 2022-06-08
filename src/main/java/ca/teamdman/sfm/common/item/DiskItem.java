package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class DiskItem extends Item {
    public DiskItem() {
        super(new Item.Properties().tab(SFMItems.TAB));
    }

    public static String getProgram(ItemStack stack) {
        return stack
                .getOrCreateTag()
                .getString("sfm:program");
    }

    public static void setProgram(ItemStack stack, String program) {
        stack
                .getOrCreateTag()
                .putString("sfm:program", program);

    }

    public static void setErrors(ItemStack stack, List<String> errors) {
        stack
                .getOrCreateTag()
                .put(
                        "sfm:errors",
                        errors
                                .stream()
                                .map(StringTag::valueOf)
                                .collect(ListTag::new, ListTag::add, ListTag::addAll)
                );
    }


    public static void setWarnings(ItemStack stack, List<String> warnings) {
        stack
                .getOrCreateTag()
                .put(
                        "sfm:warnings",
                        warnings
                                .stream()
                                .map(StringTag::valueOf)
                                .collect(ListTag::new, ListTag::add, ListTag::addAll)
                );
    }


    public static List<String> getErrors(ItemStack stack) {
        return stack
                .getOrCreateTag()
                .getList("sfm:errors", Tag.TAG_STRING)
                .stream()
                .map(StringTag.class::cast)
                .map(Tag::getAsString)
                .collect(
                        Collectors.toList());
    }

    public static List<String> getWarnings(ItemStack stack) {
        return stack
                .getOrCreateTag()
                .getList("sfm:warnings", Tag.TAG_STRING)
                .stream()
                .map(StringTag.class::cast)
                .map(Tag::getAsString)
                .collect(
                        Collectors.toList());
    }

    public static String getProgramName(ItemStack stack) {
        return stack
                .getOrCreateTag()
                .getString("sfm:name");
    }

    public static void setProgramName(ItemStack stack, String name) {
        if (stack.getItem() instanceof DiskItem) {
            stack
                    .getOrCreateTag()
                    .putString("sfm:name", name);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = getProgramName(stack);
        if (name.isEmpty()) return super.getName(stack);
        return Component.literal(name).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag detail
    ) {
        if (stack.hasTag()) {
            list.addAll(SFMLabelNBTHelper.getHoverText(stack));
            getErrors(stack)
                    .stream()
                    .map(Component::literal)
                    .map(line -> line.withStyle(ChatFormatting.RED))
                    .forEach(list::add);
            getWarnings(stack)
                    .stream()
                    .map(Component::literal)
                    .map(line -> line.withStyle(ChatFormatting.YELLOW))
                    .forEach(list::add);
        }
    }
}
