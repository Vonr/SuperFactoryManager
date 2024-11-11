package ca.teamdman.sfm.common.capabilityprovidermapper.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.IStorageMonitorableAccessor;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.capabilities.Capabilities;
import ca.teamdman.sfm.common.capabilityprovidermapper.CapabilityProviderMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class InterfaceCapabilityProviderMapper implements CapabilityProviderMapper {
    @Override
    public Optional<ICapabilityProvider> getProviderFor(LevelAccessor level, BlockPos pos) {
        var be = level.getBlockEntity(pos);
        if (!(be instanceof InterfaceBlockEntity in)) {
            return Optional.empty();
        }

        if (!in.getConfig().isEmpty() || in.getMainNode() == null || in.getGridNode() == null || !in.getGridNode().isActive()) {
            return Optional.empty();
        }

        var cap = be.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR);
        if (!cap.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new InterfaceCapabilityProvider(level, pos));
    }

    private static class InterfaceCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<IItemHandler> itemHandler;
        private final LazyOptional<IFluidHandler> fluidHandler;

        InterfaceCapabilityProvider(LevelAccessor level, BlockPos pos) {
            this.itemHandler = LazyOptional.of(() -> new InterfaceItemHandler(level, pos));
            this.fluidHandler = LazyOptional.of(() -> new InterfaceFluidHandler(level, pos));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ITEM_HANDLER) {
                return itemHandler.cast();
            } else if (cap == ForgeCapabilities.FLUID_HANDLER) {
                return fluidHandler.cast();
            }

            return LazyOptional.empty();
        }
    }

    static class InterfaceHandler {
        final LevelAccessor level;
        final BlockPos pos;

        InterfaceHandler(LevelAccessor level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Nullable
        InterfaceBlockEntity getInterface() {
            var be = level.getBlockEntity(pos);
            if (be instanceof InterfaceBlockEntity in) {
                return in;
            }
            return null;
        }

        @Nullable
        IEnergyService getEnergy() {
            var in = this.getInterface();
            if (in == null) {
                return null;
            }

            var grid = in.getMainNode().getGrid();
            if (grid == null) {
                return null;
            }

            return grid.getEnergyService();
        }

        @NotNull
        LazyOptional<IStorageMonitorableAccessor> getCapability() {
            var in = this.getInterface();
            if (in == null) {
                return LazyOptional.empty();
            }

            if (!in.getConfig().isEmpty() || in.getMainNode() == null || in.getGridNode() == null || !in.getGridNode().isActive()) {
                return LazyOptional.empty();
            }

            return in.getCapability(Capabilities.STORAGE_MONITORABLE_ACCESSOR);
        }

        void withStorage(Consumer<MEStorage> callback) {
            this.getCapability().ifPresent(t -> callback.accept(t.getInventory(IActionSource.empty())));
        }
    }

    private static class InterfaceItemHandler extends InterfaceHandler implements IItemHandler {
        InterfaceItemHandler(LevelAccessor level, BlockPos pos) {
            super(level, pos);
        }

        @Override
        public int getSlots() {
            var slots = new AtomicInteger(0);
            this.withStorage(s -> {
                int i = 0;
                for (var stored : s.getAvailableStacks()) {
                    if (stored.getKey() instanceof AEItemKey) {
                        i++;
                    }
                }
                slots.set(i);
            });

            return slots.get();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            AtomicReference<ItemStack> stack = new AtomicReference<>(ItemStack.EMPTY);
            this.withStorage(s -> {
                int i = 0;
                for (var stored : s.getAvailableStacks()) {
                    if (stored.getKey() instanceof AEItemKey key) {
                        if (slot == i++) {
                            stack.set(key.toStack((int) Math.min(Integer.MAX_VALUE, stored.getLongValue())));
                            break;
                        }
                    }
                }
            });

            return stack.get();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return stack;
            }

            var inserted = new AtomicInteger(0);
            this.withStorage(s -> {
                var key = AEItemKey.of(stack);
                if (key == null) {
                    return;
                }

                var energy = this.getEnergy();
                if (energy == null) {
                    return;
                }

                int ins = (int) StorageHelper.poweredInsert(
                        energy,
                        s,
                        key,
                        stack.getCount(),
                        IActionSource.empty(),
                        simulate ? Actionable.SIMULATE : Actionable.MODULATE
                );
                inserted.set(ins);
            });

            if (!simulate) {
                stack.shrink(inserted.get());
                return stack;
            }

            var rtn = stack.copy();
            rtn.shrink(inserted.get());
            return rtn;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0) {
                return ItemStack.EMPTY;
            }

            AtomicReference<ItemStack> stack = new AtomicReference<>(ItemStack.EMPTY);
            this.withStorage(s -> {
                int i = 0;
                for (var stored : s.getAvailableStacks()) {
                    if (stored.getKey() instanceof AEItemKey key) {
                        if (slot == i++) {
                            var energy = this.getEnergy();
                            if (energy == null) {
                                return;
                            }

                            int extracted = (int) StorageHelper.poweredExtraction(
                                    energy,
                                    s,
                                    key,
                                    amount,
                                    IActionSource.empty(),
                                    simulate ? Actionable.SIMULATE : Actionable.MODULATE
                            );

                            stack.set(key.toStack(extracted));
                            break;
                        }
                    }
                }
            });

            return stack.get();
        }

        @Override
        public int getSlotLimit(int slot) {
            return this.getStackInSlot(slot).getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    }

    private static class InterfaceFluidHandler extends InterfaceItemHandler implements IFluidHandler {
        InterfaceFluidHandler(LevelAccessor level, BlockPos pos) {
            super(level, pos);
        }

        @Override
        public int getTanks() {
            var slots = new AtomicInteger(0);
            this.withStorage(s -> {
                int i = 0;
                for (var stored : s.getAvailableStacks()) {
                    if (stored.getKey() instanceof AEFluidKey) {
                        i++;
                    }
                }
                slots.set(i);
            });

            return slots.get();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            AtomicReference<FluidStack> stack = new AtomicReference<>(FluidStack.EMPTY);

            this.withStorage(s -> {
                int i = 0;
                for (var stored : s.getAvailableStacks()) {
                    if (stored.getKey() instanceof AEFluidKey key) {
                        if (tank == i++) {
                            stack.set(key.toStack((int) Math.min(Integer.MAX_VALUE, stored.getLongValue())));
                            break;
                        }
                    }
                }
            });

            return stack.get();
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return this.getFluidInTank(tank).isFluidEqual(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            var inserted = new AtomicInteger(0);

            this.withStorage(s -> {
                var key = AEFluidKey.of(resource);
                if (key == null) {
                    return;
                }

                var energy = this.getEnergy();
                if (energy == null) {
                    return;
                }

                int ins = (int) StorageHelper.poweredInsert(
                        energy,
                        s,
                        key,
                        resource.getAmount(),
                        IActionSource.empty(),
                        fluidActionToActionable(action)
                );

                inserted.set(ins);
                if (!action.simulate()) {
                    resource.shrink(ins);
                }
            });

            return inserted.get();
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            AtomicReference<FluidStack> stack = new AtomicReference<>(FluidStack.EMPTY);

            this.withStorage(s -> {
                var key = AEFluidKey.of(resource);
                if (key == null) {
                    return;
                }

                var energy = this.getEnergy();
                if (energy == null) {
                    return;
                }

                int extracted = (int) StorageHelper.poweredExtraction(
                        energy,
                        s,
                        key,
                        resource.getAmount(),
                        IActionSource.empty(),
                        fluidActionToActionable(action)
                );

                stack.set(key.toStack(extracted));
            });

            return stack.get();
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            AtomicReference<FluidStack> stack = new AtomicReference<>(FluidStack.EMPTY);

            this.withStorage(s -> {
                for (var stored : s.getAvailableStacks()) {
                    if (stored.getKey() instanceof AEFluidKey key) {
                        var energy = this.getEnergy();
                        if (energy == null) {
                            return;
                        }

                        int extracted = (int) StorageHelper.poweredExtraction(
                                energy,
                                s,
                                key,
                                maxDrain,
                                IActionSource.empty(),
                                fluidActionToActionable(action)
                        );

                        stack.set(key.toStack(extracted));
                        break;
                    }
                }
            });

            return stack.get();
        }

        private static Actionable fluidActionToActionable(FluidAction fluidAction) {
            return switch (fluidAction) {
                case EXECUTE -> Actionable.MODULATE;
                case SIMULATE -> Actionable.SIMULATE;
            };
        }
    }
}
