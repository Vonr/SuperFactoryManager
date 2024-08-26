package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundNetworkToolUsePacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class NetworkToolItem extends Item {
    public NetworkToolItem() {
        super(new Item.Properties().stacksTo(1));
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
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag detail
    ) {
        lines.add(LocalizationKeys.NETWORK_TOOL_ITEM_TOOLTIP_1.getComponent().withStyle(ChatFormatting.GRAY));
        lines.add(LocalizationKeys.NETWORK_TOOL_ITEM_TOOLTIP_2.getComponent().withStyle(ChatFormatting.GRAY));
        lines.add(LocalizationKeys.NETWORK_TOOL_ITEM_TOOLTIP_3.getComponent(
                SFMKeyMappings.CONTAINER_INSPECTOR_KEY.get().getTranslatedKeyMessage()
        ).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pIsSelected && !pLevel.isClientSide && pEntity.tickCount % 20 == 0) {
            final long maxDistance = 128;
            CompoundTag tag = new CompoundTag();
            ListTag networks = new ListTag();
            CableNetworkManager
                    .getNetworksInRange(pLevel, pEntity.blockPosition(), maxDistance)
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
