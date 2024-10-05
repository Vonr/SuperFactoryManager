package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("DuplicatedCode")
public class ExpandedQuantityExpandedRetentionInputResourceTracker implements IInputResourceTracker {
    private final ResourceLimit resource_limit;
    private final ResourceIdSet exclusions;
    private final Int2ObjectArrayMap<Object2ObjectOpenHashMap<ResourceType<?, ?, ?>, Object2LongOpenHashMap<ResourceLocation>>>
            slot_retention_obligations_by_item = new Int2ObjectArrayMap<>();
    private final Object2ObjectOpenHashMap<ResourceType<?, ?, ?>, Object2LongOpenHashMap<ResourceLocation>>
            retention_obligations_by_item = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<ResourceType<?, ?, ?>, Object2LongOpenHashMap<ResourceLocation>>
            transferred_by_item = new Object2ObjectOpenHashMap<>();

    public ExpandedQuantityExpandedRetentionInputResourceTracker(
            ResourceLimit resourceLimit,
            ResourceIdSet exclusions
    ) {
        this.resource_limit = resourceLimit;
        this.exclusions = exclusions;
    }

    @Override
    public <STACK, CAP, ITEM> boolean isDone(
            ResourceType<STACK, ITEM, CAP> type,
            STACK stack
    ) {
        long can_transfer = resource_limit.limit().quantity().number().value();
        long transferred_for_item = 0;
        var transferred_for_resource_type = transferred_by_item.get(type);
        if (transferred_for_resource_type != null) {
            ResourceLocation item_id = type.getRegistryKey(stack);
            transferred_for_item = transferred_for_resource_type.getLong(item_id);
        }
        return transferred_for_item >= can_transfer;
    }

    @Override
    public ResourceLimit getResourceLimit() {
        return resource_limit;
    }

    @Override
    public ResourceIdSet getExclusions() {
        return exclusions;
    }

    @Override
    public <STACK, ITEM, CAP> long getRetentionObligationForSlot(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            int slot
    ) {
        var resourceTypeEntry = slot_retention_obligations_by_item.get(slot);
        if (resourceTypeEntry != null) {
            ResourceLocation item_id = resourceType.getRegistryKey(stack);
            var itemEntry = resourceTypeEntry.get(resourceType);
            if (itemEntry != null) {
                return itemEntry.getLong(item_id);
            }
        }
        return 0;
    }

    @Override
    public <STACK, ITEM, CAP> long getRemainingRetentionObligation(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack
    ) {
        long retention = resource_limit.limit().retention().number().value();
        long retained_for_item = 0;
        // don't use getOrDefault to avoid allocations
        var retained_for_resource_type = retention_obligations_by_item.get(resourceType);
        if (retained_for_resource_type != null) {
            ResourceLocation item_id = resourceType.getRegistryKey(stack);
            if (retained_for_resource_type.containsKey(item_id)) {
                retained_for_item = retained_for_resource_type.getLong(item_id);
            }
        }
        return retention - retained_for_item;
    }


    @Override
    public <STACK, ITEM, CAP> void trackRetentionObligation(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            int slot,
            long promise
    ) {
        ResourceLocation item_id = resourceType.getRegistryKey(stack);
        retention_obligations_by_item.computeIfAbsent(resourceType, k -> new Object2LongOpenHashMap<>())
                .addTo(item_id, promise);
        slot_retention_obligations_by_item.computeIfAbsent(slot, k -> new Object2ObjectOpenHashMap<>())
                .computeIfAbsent(resourceType, k -> new Object2LongOpenHashMap<>())
                .addTo(item_id, promise);
    }

    @Override
    public <STACK, ITEM, CAP> long getMaxTransferable(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack
    ) {
        long max_transfer = resource_limit.limit().quantity().number().value();
        long transferred_for_item = 0;
        var transferred_for_resource_type = transferred_by_item.get(resourceType);
        if (transferred_for_resource_type != null) {
            ResourceLocation item_id = resourceType.getRegistryKey(stack);
            transferred_for_item = transferred_for_resource_type.getLong(item_id);
        }
        return max_transfer - transferred_for_item;
    }

    @Override
    public <STACK, ITEM, CAP> void trackTransfer(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            long amount
    ) {
        ResourceLocation item_id = resourceType.getRegistryKey(stack);
        transferred_by_item.computeIfAbsent(resourceType, k -> new Object2LongOpenHashMap<>())
                .addTo(item_id, amount);
    }
}
