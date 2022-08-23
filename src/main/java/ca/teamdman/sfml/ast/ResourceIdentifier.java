package ca.teamdman.sfml.ast;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;

public record ResourceIdentifier(
        String type,
        String domain,
        String value
) implements ASTNode, Predicate<ItemStack> {
    public static final ResourceIdentifier MATCH_ALL = new ResourceIdentifier("*", "*");

    public ResourceIdentifier(String value) {
        this("item", "minecraft", value);
    }

    public ResourceIdentifier(String namespace, String value) {
        this("item", namespace, value);
    }

    public static ResourceIdentifier fromString(String string) {
        var parts = string.split(":");
        if (parts.length == 1) {
            return new ResourceIdentifier(parts[0]);
        } else if (parts.length == 2) {
            return new ResourceIdentifier(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new ResourceIdentifier(parts[0], parts[1], parts[2]);
        } else {
            throw new IllegalArgumentException("bad resource id");
        }
    }

    public boolean test(ItemStack stack) {
        if (domain.equals("*") && value.equals("*")) return true;
        var reg = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (reg == null) return false;
        if (domain.equals("*")) return value.equals(reg.getPath());
        if (value.equals("*")) return domain.equals(reg.getNamespace());
        return domain.equals(reg.getNamespace()) && value.equals(reg.getPath());
    }

    @Override
    public String toString() {
        return type + ":" + domain + ":" + value;
    }
}
