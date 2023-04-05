package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.LabelAccess;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Stream;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class GasResourceType extends ResourceType<GasStack, IGasHandler> {
    public static final Capability<IGasHandler> GAS_HANDLER = get(new CapabilityToken<>() {
    });

    public GasResourceType() {
        super(GAS_HANDLER);
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return ForgeRegistries.ITEMS.containsKey(location);
    }

    @Override
    public ResourceLocation getRegistryKey(ItemStack itemStack) {
        return ForgeRegistries.ITEMS.getKey(itemStack.getItem());
    }

    @Override
    public int getCount(ItemStack stack) {
        return stack.getCount();
    }

    @Override
    public ItemStack getStackInSlot(IItemHandler cap, int slot) {
        return cap.getStackInSlot(slot);
    }

    @Override
    public ItemStack extract(IItemHandler handler, int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof ItemStack;
    }

    @Override
    public boolean matchesCapType(Object o) {
        return o instanceof IItemHandler;
    }

    @Override
    public int getSlots(IItemHandler handler) {
        return handler.getSlots();
    }

    /**
     * @param handler
     * @param slot
     * @param stack
     * @param simulate
     * @return remaining stack that was not inserted
     */
    @Override
    public ItemStack insert(IItemHandler handler, int slot, ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    @Override
    public Stream<ItemStack> collect(IItemHandler cap, LabelAccess labelAccess) {
        var rtn = Stream.<ItemStack>builder();
        for (int slot = 0; slot < cap.getSlots(); slot++) {
            if (!labelAccess.slots().contains(slot)) continue;
            var stack = cap.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            rtn.add(stack);
        }
        return rtn.build();
    }
}
