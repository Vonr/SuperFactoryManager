package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundNetworkToolUsePacket;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkToolItem extends Item {
    public NetworkToolItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext pContext) {
        if (pContext.getLevel().isClientSide || pContext.getHand() == InteractionHand.OFF_HAND) return InteractionResult.SUCCESS;

        PacketDistributor.sendToServer(new ServerboundNetworkToolUsePacket(
                pContext.getClickedPos(),
                pContext.getClickedFace()
        ));
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack pStack,
            TooltipContext pContext,
            List<Component> pTooltipComponents,
            TooltipFlag pTooltipFlag
    ) {
        pTooltipComponents.add(LocalizationKeys.NETWORK_TOOL_ITEM_TOOLTIP_1.getComponent().withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(LocalizationKeys.NETWORK_TOOL_ITEM_TOOLTIP_2.getComponent().withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(LocalizationKeys.NETWORK_TOOL_ITEM_TOOLTIP_3.getComponent(
                SFMKeyMappings.CONTAINER_INSPECTOR_KEY.get().getTranslatedKeyMessage()
        ).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pIsSelected || pLevel.isClientSide || pEntity.tickCount % 20 != 0) {
            return;
        }
        final long maxDistance = 128;
        Set<BlockPos> cablePositions = new HashSet <>();
        Set<BlockPos> capabilityPositions = new HashSet <>();
        CableNetworkManager
                .getNetworksInRange(pLevel, pEntity.blockPosition(), maxDistance)
                .forEach(net -> {
                    net.getCablePositions().forEach(cablePositions::add);
                    net.getCapabilityProviderPositions().forEach(capabilityPositions::add);
                });
        stack.set(SFMDataComponents.CABLE_POSITIONS, cablePositions);
        stack.set(SFMDataComponents.CAPABILITY_POSITIONS, capabilityPositions);
    }
}
