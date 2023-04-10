package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// resourceTypeName resourceNamespace, resourceTypeName name, resource resourceNamespace, resource name
// sfm:item:minecraft:stone
public record ResourceIdentifier<STACK, ITEM, CAP>(
        String resourceTypeNamespace,
        String resourceTypeName,
        String resourceNamespace,
        String resourceName
) implements ASTNode, Predicate<Object> {

    public static final  ResourceIdentifier<?, ?, ?>                     MATCH_ALL   = new ResourceIdentifier<>(
            ".*",
            ".*"
    );
    private static final Map<String, Map<String, ResourceType<?, ?, ?>>> lookupCache = new Object2ObjectOpenHashMap<>();

    public ResourceIdentifier(String value) {
        this(SFM.MOD_ID, "item", "minecraft", value);
    }

    public ResourceIdentifier(String namespace, String value) {
        this(SFM.MOD_ID, "item", namespace, value);
    }

    public ResourceIdentifier(String typeName, String resourceNamespace, String resourceName) {
        this(SFM.MOD_ID, typeName, resourceNamespace, resourceName);
    }

    public static <STACK, ITEM, CAP> ResourceIdentifier<STACK, ITEM, CAP> fromString(String string) {
        var parts = string.split(":");
        if (parts.length == 1) {
            return new ResourceIdentifier<>(parts[0]);
        } else if (parts.length == 2) {
            return new ResourceIdentifier<>(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new ResourceIdentifier<>(parts[0], parts[1], parts[2]);
        } else if (parts.length == 4) {
            return new ResourceIdentifier<>(parts[0], parts[1], parts[2], parts[3]);
        } else {
            throw new IllegalArgumentException("bad resource id");
        }
    }

    public void assertValid() throws IllegalArgumentException {
        try {
            Pattern.compile(this.resourceNamespace);
            Pattern.compile(this.resourceName);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid resource identifier pattern \""
                                               + this
                                               + "\" - "
                                               + e.getMessage());
        }
    }

    public Optional<ResourceLocation> getLocation() {
        try {
            return Optional.of(new ResourceLocation(resourceNamespace, resourceName));
        } catch (ResourceLocationException e) {
            return Optional.empty();
        }
    }

    public boolean test(Object other) {
        return getResourceType().test(this, other);
    }

    public ResourceType<STACK, ITEM, CAP> getResourceType() {
        var namespaceMap = lookupCache.get(this.resourceTypeNamespace);
        if (namespaceMap == null) {
            namespaceMap = new Object2ObjectOpenHashMap<>();
            lookupCache.put(this.resourceTypeNamespace, namespaceMap);
        }
        var type = namespaceMap.get(this.resourceTypeName);
        if (type == null) {
            type = SFMResourceTypes.DEFERRED_TYPES
                    .get()
                    .getValue(new ResourceLocation(this.resourceTypeNamespace, this.resourceTypeName));
            namespaceMap.put(this.resourceTypeName, type);
        }
        return (ResourceType<STACK, ITEM, CAP>) type;
    }

    @Override
    public String toString() {
        return resourceTypeNamespace + ":" + resourceTypeName + ":" + resourceNamespace + ":" + resourceName;
    }
}
