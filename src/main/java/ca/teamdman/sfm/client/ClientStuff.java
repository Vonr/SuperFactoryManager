package ca.teamdman.sfm.client;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.screen.*;
import ca.teamdman.sfm.client.model.CableBlockModelWrapper;
import ca.teamdman.sfm.client.render.CableFacadeBlockColor;
import ca.teamdman.sfm.client.render.PrintingPressBlockEntityRenderer;
import ca.teamdman.sfm.common.block.CableFacadeBlock;
import ca.teamdman.sfm.common.block.ManagerBlock;
import ca.teamdman.sfm.common.blockentity.CableFacadeBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundFacadePacket;
import ca.teamdman.sfm.common.net.ServerboundManagerLogDesireUpdatePacket;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.FacadeType;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientStuff {

    public static void setOrPushScreen(Screen screen) {
        if (Minecraft.getInstance().screen == null) {
            Minecraft
                    .getInstance()
                    .setScreen(screen);
        } else {
            Minecraft
                    .getInstance()
                    .pushGuiLayer(screen);
        }
    }

    public static void showLabelGunScreen(
            ItemStack stack,
            InteractionHand hand
    ) {
        setOrPushScreen(new LabelGunScreen(stack, hand));
    }

    public static void showProgramEditScreen(
            String initialContent,
            Consumer<String> saveCallback
    ) {
        ProgramEditScreen screen = new ProgramEditScreen(initialContent, saveCallback);
        setOrPushScreen(screen);
        screen.scrollToTop();
    }

    public static void showProgramEditScreen(String initialContent) {
        showProgramEditScreen(initialContent, (x) -> {
        });
    }

    public static void showExampleListScreen(
            String program,
            Consumer<String> saveCallback
    ) {
        setOrPushScreen(new ExamplesScreen((chosenTemplate, templates) -> showExampleEditScreen(
                program,
                chosenTemplate,
                templates,
                saveCallback
        )));
    }

    public static void showExampleEditScreen(
            String program,
            String chosenTemplate,
            Map<String, String> templates,
            Consumer<String> saveCallback
    ) {
        ProgramEditScreen screen = new ExampleEditScreen(program, chosenTemplate, templates, saveCallback);
        setOrPushScreen(screen);
        screen.scrollToTop();
    }

    public static void showLogsScreen(ManagerContainerMenu menu) {
        LogsScreen screen = new LogsScreen(menu);
        setOrPushScreen(screen);
        screen.scrollToBottom();
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerLogDesireUpdatePacket(
                menu.containerId,
                menu.MANAGER_POSITION,
                true
        ));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY.get(),
                PrintingPressBlockEntityRenderer::new
        );
    }

    public static boolean isKeyDown(Lazy<KeyMapping> key) {
        // special effort is needed to ensure this works properly when the manager screen is open
        // https://github.com/mekanism/Mekanism/blob/f92b48a49e0766cd3aa78e95c9c4a47ba90402f5/src/main/java/mekanism/client/key/MekKeyHandler.java
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(
                handle,
                key.get().getKey().getValue()
        );
    }

    public static @Nullable BlockEntity getLookBlockEntity() {
        if (!FMLEnvironment.dist.isClient()) {
            throw new IllegalCallerException("getLookBlockEntity must be called on client");
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        HitResult hr = Minecraft.getInstance().hitResult;
        if (hr == null) return null;
        if (hr.getType() != HitResult.Type.BLOCK) return null;
        var pos = ((BlockHitResult) hr).getBlockPos();
        return level.getBlockEntity(pos);
    }

    public static String resolveTranslation(TranslatableContents contents) {
        return I18n.get(contents.getKey(), contents.getArgs());
    }

    // TODO: chat message for feedback that something happened
    // TODO: copy item id, not just NBT
    // TODO: replace with showing a screen with the data
    public static void showItemInspectorScreen(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            String content = tag.toString();
            Minecraft.getInstance().keyboardHandler.setClipboard(content);
            SFM.LOGGER.info("Copied {} characters to clipboard", content.length());
        }
    }

    @SubscribeEvent
    public static void onModelBakeEvent(ModelEvent.BakingCompleted event) {
        event.getModels().computeIfPresent(
                BlockModelShaper.stateToModelLocation(SFMBlocks.CABLE_FACADE_BLOCK.get().defaultBlockState()),
                (location, model) -> new CableBlockModelWrapper(model)
        );
        event.getModels().computeIfPresent(
                BlockModelShaper.stateToModelLocation(SFMBlocks.CABLE_FACADE_BLOCK.get().defaultBlockState()
                                                              .setValue(
                                                                      CableFacadeBlock.FACADE_TYPE_PROP,
                                                                      FacadeType.OPAQUE
                                                              )),
                (location, model) -> new CableBlockModelWrapper(model)
        );
        event.getModels().computeIfPresent(
                BlockModelShaper.stateToModelLocation(SFMBlocks.CABLE_FACADE_BLOCK.get().defaultBlockState()
                                                              .setValue(
                                                                      CableFacadeBlock.FACADE_TYPE_PROP,
                                                                      FacadeType.TRANSLUCENT
                                                              )),
                (location, model) -> new CableBlockModelWrapper(model)
        );
    }

    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
        event.register(new CableFacadeBlockColor(), SFMBlocks.CABLE_FACADE_BLOCK.get());
    }

    public static void sendFacadePacketFromClientWithConfirmationIfNecessary(ServerboundFacadePacket msg) {
        // Given the incentives for a single cable network to be used,
        // we want to protect users from accidentally clobbering their designs in a single action
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        assert player != null;

        // Determine if a confirmation is necessary
        switch (msg.spreadLogic()) {
            case SINGLE -> {
                // No confirmation necessary for single updates
                SFMPackets.CABLE_CHANNEL.sendToServer(msg);
                // Perform eager update
                ServerboundFacadePacket.handle(msg, player);
            }
            case NETWORK -> {
                // Confirm if:
                // - There exists two cable blocks with different facade status
                // Do not confirm if:
                // - All the cable blocks are the same facade status
                Level level = player.level;
                Stream<BlockPos> toFacade = ServerboundFacadePacket.gatherCableBlocksToFacade(
                        msg.spreadLogic(),
                        level,
                        msg.pHitResult().getBlockPos()
                );
                Object2IntOpenHashMap<BlockState> clobbering = new Object2IntOpenHashMap<>();
                for (BlockPos blockPos : ((Iterable<BlockPos>) toFacade::iterator)) {
                    if (level.getBlockEntity(blockPos) instanceof CableFacadeBlockEntity facadeBlockEntity) {
                        clobbering.merge(
                                facadeBlockEntity.getFacadeState(),
                                1,
                                Integer::sum
                        );
                    } else {
                        BlockState blockState = level.getBlockState(blockPos);
                        if (blockState.getBlock() instanceof ManagerBlock) continue;
                        clobbering.merge(
                                blockState,
                                1,
                                Integer::sum
                        );
                    }
                }
                int uniqueStateCount = clobbering.keySet().size();
                if (uniqueStateCount > 1) {
                    // Confirmation necessary
                    ConfirmScreen confirmScreen = new ConfirmScreen(
                            (confirmed) -> {
                                minecraft.popGuiLayer(); // Close confirm screen
                                if (confirmed) {
                                    SFMPackets.CABLE_CHANNEL.sendToServer(msg);
                                    // Perform eager update
                                    ServerboundFacadePacket.handle(msg, player);
                                }
                            },
                            LocalizationKeys.FACADE_APPLY_NETWORK_CONFIRM_SCREEN_TITLE.getComponent(),
                            LocalizationKeys.FACADE_APPLY_NETWORK_CONFIRM_SCREEN_MESSAGE.getComponent(
                                    uniqueStateCount,
                                    clobbering.values().intStream().sum()
                            ),
                            LocalizationKeys.FACADE_APPLY_NETWORK_CONFIRM_SCREEN_YES_BUTTON.getComponent(),
                            LocalizationKeys.FACADE_APPLY_NETWORK_CONFIRM_SCREEN_NO_BUTTON.getComponent()
                    );
                    setOrPushScreen(confirmScreen);
                    confirmScreen.setDelay(10);
                } else {
                    // No confirmation necessary
                    SFMPackets.CABLE_CHANNEL.sendToServer(msg);
                    // Perform eager update
                    ServerboundFacadePacket.handle(msg, player);
                }
            }
            case NETWORK_GLOBAL_SAME_BLOCK, NETWORK_CONTIGUOUS_SAME_BLOCK -> {
                // Confirm if the placement of this new facade will touch existing facades of the new type
                // So like AAABBBAAA -> AAAAAAAAA should warn
                // but     AAABBBAAA -> AAACCCAAA should not warn
                Level level = player.level;
                Stream<BlockPos> toFacade = ServerboundFacadePacket.gatherCableBlocksToFacade(
                        msg.spreadLogic(),
                        level,
                        msg.pHitResult().getBlockPos()
                );

                // Get paint block state
                ItemStack paintStack = player.getMainHandItem();
                Block paintBlock = ServerboundFacadePacket.getBlockFromStack(
                        paintStack,
                        level,
                        msg.pHitResult().getBlockPos()
                );
                if (paintBlock == null) return;
                BlockPlaceContext blockPlaceContext = new BlockPlaceContext(
                        player,
                        InteractionHand.MAIN_HAND,
                        paintStack,
                        msg.pHitResult()
                );
                BlockState paintBlockState = Objects.requireNonNullElse(
                        paintBlock.getStateForPlacement(blockPlaceContext),
                        paintBlock.defaultBlockState()
                );

                int susTouches = 0;
                for (BlockPos blockPos : ((Iterable<BlockPos>) toFacade::iterator)) {
                    BlockState cableState = level.getBlockState(blockPos);
                    if (cableState.getBlock() instanceof ManagerBlock) continue;
                    // Skip if this is already the desired state
                    if (level.getBlockEntity(blockPos) instanceof CableFacadeBlockEntity facadeBlockEntity
                        && facadeBlockEntity.getFacadeState().equals(paintBlockState)) continue;
                    // Increment if neighbour already in the new state
                    for (Direction direction : Direction.values()) {
                        BlockPos offset = blockPos.relative(direction);
                        if (level.getBlockEntity(offset) instanceof CableFacadeBlockEntity facadeBlockEntity
                            && facadeBlockEntity.getFacadeState().equals(paintBlockState)) {
                            susTouches++;
                            break;
                        }
                    }
                }
                if (susTouches > 0) {
                    // Confirmation necessary
                    ConfirmScreen confirmScreen = new ConfirmScreen(
                            (confirmed) -> {
                                minecraft.popGuiLayer(); // Close confirm screen
                                if (confirmed) {
                                    SFMPackets.CABLE_CHANNEL.sendToServer(msg);
                                    // Perform eager update
                                    ServerboundFacadePacket.handle(msg, player);
                                }
                            },
                            LocalizationKeys.FACADE_APPLY_SUS_NEIGHBOURS_CONFIRM_SCREEN_TITLE.getComponent(),
                            LocalizationKeys.FACADE_APPLY_SUS_NEIGHBOURS_CONFIRM_SCREEN_MESSAGE.getComponent(
                                    susTouches,
                                    susTouches
                            ),
                            LocalizationKeys.FACADE_APPLY_SUS_NEIGHBOURS_CONFIRM_SCREEN_YES_BUTTON.getComponent(),
                            LocalizationKeys.FACADE_APPLY_SUS_NEIGHBOURS_CONFIRM_SCREEN_NO_BUTTON.getComponent()
                    );
                    setOrPushScreen(confirmScreen);
                    confirmScreen.setDelay(10);
                } else {
                    // No confirmation necessary
                    SFMPackets.CABLE_CHANNEL.sendToServer(msg);
                    // Perform eager update
                    ServerboundFacadePacket.handle(msg, player);
                }
            }
        }
    }

}
