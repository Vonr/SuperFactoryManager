package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.ResourceType;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public record ResourceIdentifier<STACK, CAP>(
        String type,
        String domain,
        String value
) implements ASTNode, Predicate<Object> {

    public static final ResourceIdentifier<?, ?> MATCH_ALL = new ResourceIdentifier<>("*", "*");

    public ResourceIdentifier(String value) {
        this("item", "minecraft", value);
    }

    public ResourceIdentifier(String namespace, String value) {
        this("item", namespace, value);
    }

    public static <STACK, CAP> ResourceIdentifier<STACK, CAP> fromString(String string) {
        var parts = string.split(":");
        if (parts.length == 1) {
            return new ResourceIdentifier<>(parts[0]);
        } else if (parts.length == 2) {
            return new ResourceIdentifier<>(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new ResourceIdentifier<>(parts[0], parts[1], parts[2]);
        } else {
            throw new IllegalArgumentException("bad resource id");
        }
    }

    public ResourceLocation getLocation() {
        return new ResourceLocation(domain, value);
    }

    public boolean test(Object other) {
        return getType().test(this, other);
    }

    public ResourceType<STACK, CAP> getType() {
        // in the off chance someone other than me is looking at this,
        // I hope hardcoding the SFM MOD ID here isn't causing problems.
        return (ResourceType<STACK, CAP>) SFMResourceTypes.DEFERRED_TYPES
                .get()
                .getValue(new ResourceLocation(SFM.MOD_ID, this.type));
    }

    @Override
    public String toString() {
        return type + ":" + domain + ":" + value;
    }
}
