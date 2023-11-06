package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.net.ServerboundNetworkToolUsePacket;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class NetworkToolItem extends Item {
    public NetworkToolItem() {
        super(new Item.Properties().stacksTo(1).tab(SFMItems.TAB));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext pContext) {
        if (pContext.getLevel().isClientSide) return InteractionResult.SUCCESS;

        SFMPackets.INSPECTION_CHANNEL.sendToServer(new ServerboundNetworkToolUsePacket(
                pContext.getClickedPos(),
                pContext.getClickedFace()
        ));
        return InteractionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pIsSelected && !pLevel.isClientSide && pEntity.tickCount % 20 == 0) {
            final long maxDistance = 128;
            CompoundTag tag = new CompoundTag();
            ListTag networks = new ListTag();
            CableNetworkManager
                    .getNetworksForLevel(pLevel)
                    .filter(net -> net
                            .getCablePositions()
                            .anyMatch(cablePos -> cablePos.distSqr(pEntity.blockPosition())
                                                  < maxDistance * maxDistance))
                    .forEach(net -> {
                        CompoundTag networkTag = new CompoundTag();
                        networkTag.put(
                                "cable_positions",
                                net
                                        .getCablePositions()
                                        .map(NbtUtils::writeBlockPos)
                                        .collect(ListTag::new, ListTag::add, ListTag::addAll)
                        );
                        networkTag.put(
                                "capability_provider_positions",
                                net
                                        .getCapabilityProviderPositions()
                                        .map(NbtUtils::writeBlockPos)
                                        .collect(ListTag::new, ListTag::add, ListTag::addAll)
                        );
                        networks.add(networkTag);
                    });
            tag.put("networks", networks);
            pStack.setTag(tag);
        }
    }
}
