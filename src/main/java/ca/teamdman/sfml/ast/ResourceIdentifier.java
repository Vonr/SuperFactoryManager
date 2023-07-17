package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// resourceTypeName resourceNamespace, resourceTypeName name, resource resourceNamespace, resource name
// sfm:item:minecraft:stone
public class ResourceIdentifier<STACK, ITEM, CAP> implements ASTNode, Predicate<Object> {

    public static final ResourceIdentifier<?, ?, ?> MATCH_ALL = new ResourceIdentifier<>(
            ".*",
            ".*"
    );

    public final String resourceTypeNamespace;
    public final String resourceTypeName;
    public final String resourceNamespace;
    public final String resourceName;
    private @Nullable ResourceType<STACK, ITEM, CAP> resourceTypeCache = null;

    public ResourceIdentifier(
            String resourceTypeNamespace,
            String resourceTypeName,
            String resourceNamespace,
            String resourceName
    ) {
        this.resourceTypeNamespace = resourceTypeNamespace;
        this.resourceTypeName = resourceTypeName;
        this.resourceNamespace = resourceNamespace;
        this.resourceName = resourceName;
    }

    public ResourceIdentifier(String value) {
        this(SFM.MOD_ID, "item", ".*", value);
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
            throw new IllegalArgumentException("bad resource id: " + string);
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

    private static final Map<ResourceIdentifier<?, ?, ?>, List<ResourceIdentifier<?, ?, ?>>> expansionCache = new Object2ObjectOpenHashMap<>();

    public boolean test(Object other) {
        ResourceType<STACK, ITEM, CAP> resourceType = getResourceType();
        return resourceType != null && resourceType.stackMatches(this, other);
    }

    public List<ResourceIdentifier<STACK, ITEM, CAP>> expand() {
        if (expansionCache.containsKey(this)) {
            //noinspection unchecked,rawtypes
            return (List<ResourceIdentifier<STACK, ITEM, CAP>>) (List) expansionCache.get(this);
        }
        ResourceType<STACK, ITEM, CAP> resourceType = getResourceType();
        //noinspection DataFlowIssue // if we get here, it should have a registry
        List<ResourceIdentifier<STACK, ITEM, CAP>> rtn = resourceType.getRegistry().getEntries().stream()
                .filter(e -> ResourceType.stackIdMatches(this, e.getKey().location()))
                .map(e -> new ResourceIdentifier<STACK, ITEM, CAP>(
                        resourceTypeNamespace,
                        resourceTypeName,
                        e.getKey().location().getNamespace(),
                        e.getKey().location().getPath()
                )).toList();
        //noinspection unchecked,rawtypes
        expansionCache.put(this, (List) rtn);
        return rtn;
    }

    @SuppressWarnings("unchecked")
    public @Nullable ResourceType<STACK, ITEM, CAP> getResourceType() {
        if (resourceTypeCache == null) {
            resourceTypeCache = (ResourceType<STACK, ITEM, CAP>) SFMResourceTypes.DEFERRED_TYPES
                    .get()
                    .getValue(new ResourceLocation(this.resourceTypeNamespace, this.resourceTypeName));
        }
        return resourceTypeCache;
    }

    @Override
    public String toString() {
        return resourceTypeNamespace + ":" + resourceTypeName + ":" + resourceNamespace + ":" + resourceName;
    }

    public String toStringCondensed() {
        String rtn;
        if (getResourceType() == SFMResourceTypes.ITEM.get()) {
            if (resourceNamespace.equals(".*")) {
                rtn = resourceName;
            } else {
                rtn = resourceNamespace + ":" + resourceName;
            }
        } else if (getResourceType() == SFMResourceTypes.FORGE_ENERGY.get() && getLocation()
                .filter(rl -> rl.equals(new ResourceLocation("forge", "energy")))
                .isPresent()) {
            rtn = "forge_energy::";
        } else if (resourceTypeNamespace.equals(SFM.MOD_ID)) {
            rtn = resourceTypeName + ":" + resourceNamespace + ":" + resourceName;
        } else {
            rtn = resourceTypeNamespace + ":" + resourceTypeName + ":" + resourceNamespace + ":" + resourceName;
        }
        return "\"" + rtn + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceIdentifier<?, ?, ?> that = (ResourceIdentifier<?, ?, ?>) o;
        return Objects.equals(resourceTypeNamespace, that.resourceTypeNamespace)
               && Objects.equals(resourceTypeName, that.resourceTypeName)
               && Objects.equals(resourceNamespace, that.resourceNamespace)
               && Objects.equals(resourceName, that.resourceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceTypeNamespace, resourceTypeName, resourceNamespace, resourceName);
    }
}
