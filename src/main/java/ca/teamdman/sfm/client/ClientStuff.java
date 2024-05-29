package ca.teamdman.sfm.client;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.screen.LabelGunScreen;
import ca.teamdman.sfm.client.gui.screen.LogsScreen;
import ca.teamdman.sfm.client.gui.screen.ProgramEditScreen;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.client.render.PrintingPressBlockEntityRenderer;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.logging.TranslatableLogEvent;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfm.common.net.ClientboundManagerGuiUpdatePacket;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientStuff {
    public static void showLabelGunScreen(ItemStack stack, InteractionHand hand) {
        Minecraft
                .getInstance()
                .setScreen(new LabelGunScreen(stack, hand));
    }

    public static void showProgramEditScreen(String initialContent, Consumer<String> callback) {
        ProgramEditScreen screen = new ProgramEditScreen(initialContent, callback);
        if (Minecraft.getInstance().screen == null) {
            Minecraft
                    .getInstance()
                    .setScreen(screen);
        } else {
            Minecraft
                    .getInstance()
                    .pushGuiLayer(screen);
        }
        screen.scrollToTop();
    }

    public static void showProgramEditScreen(ItemStack diskItem, Consumer<String> callback) {
        showProgramEditScreen(DiskItem.getProgram(diskItem), callback);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY.get(),
                PrintingPressBlockEntityRenderer::new
        );
    }

    public static boolean isMoreInfoKeyDown() {
        // special effort is needed to ensure this works properly when the manager screen is open
        // https://github.com/mekanism/Mekanism/blob/f92b48a49e0766cd3aa78e95c9c4a47ba90402f5/src/main/java/mekanism/client/key/MekKeyHandler.java
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(
                handle,
                SFMKeyMappings.MORE_INFO_TOOLTIP_KEY
                        .get()
                        .getKey()
                        .getValue()
        );
    }

    public static void updateMenu(ClientboundManagerGuiUpdatePacket msg) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        var container = player.containerMenu;
        if (container instanceof ManagerContainerMenu menu && container.containerId == msg.windowId()) {
            menu.tickTimeNanos = msg.tickTimes();
            menu.state = msg.state();
            menu.program = msg.program();

            if (menu.logs.isEmpty()) {
                menu.logs = msg.logs();
            } else {
                var newest = menu.logs.get(menu.logs.size() - 1).instant();
                List<TranslatableLogEvent> toAdd = msg.logs().stream()
                        .filter(x -> TranslatableLogger.comesAfter(x.instant(), newest))
                        .toList();
                menu.logs.addAll(toAdd);
            }
        }
    }

    public static @Nullable BlockEntity getLookBlockEntity() {
        assert FMLEnvironment.dist.isClient();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        HitResult hr = Minecraft.getInstance().hitResult;
        if (hr == null) return null;
        if (hr.getType() != HitResult.Type.BLOCK) return null;
        var pos = ((BlockHitResult) hr).getBlockPos();
        return level.getBlockEntity(pos);
    }

    public static void showLogsScreen(ManagerContainerMenu menu) {
        LogsScreen screen = new LogsScreen(menu);
        if (Minecraft.getInstance().screen == null) {
            Minecraft
                    .getInstance()
                    .setScreen(screen);
        } else {
            Minecraft
                    .getInstance()
                    .pushGuiLayer(screen);
        }
        screen.scrollToBottom();
    }

    public static String resolveTranslation(TranslatableContents contents) {
        return I18n.get(contents.getKey(), contents.getArgs());
    }
}
