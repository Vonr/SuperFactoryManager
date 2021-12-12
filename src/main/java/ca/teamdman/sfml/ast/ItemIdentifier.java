package ca.teamdman.sfml.ast;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record ItemIdentifier(
        String domain,
        String value
) implements ASTNode, Predicate<ItemStack> {
    public static final ItemIdentifier MATCH_ALL = new ItemIdentifier("*", "*");

    public ItemIdentifier(String value) {
        this("minecraft", value);
    }

    public boolean test(ItemStack stack) {
        if (domain.equals("*") && value.equals("*")) return true;
        var reg = stack.getItem().getRegistryName();
        if (reg == null) return false;
        if (domain.equals("*")) return value.equals(reg.getPath());
        if (value.equals("*")) return domain.equals(reg.getNamespace());
        return domain.equals(reg.getNamespace()) && value.equals(reg.getPath());
    }
}
