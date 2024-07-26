package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.net.ServerboundDiskItemSetProgramPacket;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramLinter;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DiskItem extends Item {

    public DiskItem() {
        super(new Item.Properties());
    }

    public static String getProgram(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.PROGRAM_STRING, "");
    }

    public static void setProgram(
            ItemStack stack,
            String programString
    ) {
        programString = programString.replaceAll("\r", "");
        stack.set(SFMDataComponents.PROGRAM_STRING, programString);
    }

    public static void clearData(ItemStack stack) {
        stack.remove(SFMDataComponents.PROGRAM_STRING);
        stack.remove(SFMDataComponents.PROGRAM_ERRORS);
        stack.remove(SFMDataComponents.PROGRAM_WARNINGS);
        stack.remove(SFMDataComponents.LABEL_POSITION_HOLDER);
    }

    public static Optional<Program> compileAndUpdateErrorsAndWarnings(
            ItemStack stack,
            @Nullable ManagerBlockEntity manager
    ) {
        if (manager != null) {
            manager.logger.info(x -> x.accept(Constants.LocalizationKeys.PROGRAM_COMPILE_FROM_DISK_BEGIN.get()));
        }
        AtomicReference<Program> rtn = new AtomicReference<>(null);
        Program.compile(
                getProgram(stack),
                successProgram -> {
                    ArrayList<TranslatableContents> warnings = ProgramLinter.gatherWarnings(
                            successProgram,
                            LabelPositionHolder.from(stack),
                            manager
                    );

                    // Log to disk
                    if (manager != null) {
                        manager.logger.info(x -> x.accept(Constants.LocalizationKeys.PROGRAM_COMPILE_SUCCEEDED_WITH_WARNINGS.get(
                                successProgram.name(),
                                warnings.size()
                        )));
                        manager.logger.warn(warnings::forEach);
                    }

                    // Update disk properties
                    setProgramName(stack, successProgram.name());
                    setWarnings(stack, warnings);
                    setErrors(stack, Collections.emptyList());

                    // Track result
                    rtn.set(successProgram);
                },
                errors -> {
                    List<TranslatableContents> warnings = Collections.emptyList();

                    // Log to disk
                    if (manager != null) {
                        manager.logger.error(x -> x.accept(Constants.LocalizationKeys.PROGRAM_COMPILE_FAILED_WITH_ERRORS.get(
                                errors.size())));
                        manager.logger.error(errors::forEach);
                    }

                    // Update disk properties
                    setWarnings(stack, warnings);
                    setErrors(stack, errors);
                }
        );
        return Optional.ofNullable(rtn.get());
    }

    public static List<Component> getErrors(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.PROGRAM_ERRORS, Collections.emptyList());
    }

    public static void setErrors(
            ItemStack stack,
            List<TranslatableContents> errors
    ) {
        stack.set(SFMDataComponents.PROGRAM_ERRORS, errors
                .stream()
                .map(MutableComponent::create)
                .collect(Collectors.toList()));
    }

    public static List<Component> getWarnings(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.PROGRAM_WARNINGS, Collections.emptyList());
    }

    public static void setWarnings(
            ItemStack stack,
            List<TranslatableContents> warnings
    ) {
        stack.set(SFMDataComponents.PROGRAM_WARNINGS, warnings
                .stream()
                .map(MutableComponent::create)
                .collect(Collectors.toList()));
    }

    public static void setProgramName(
            ItemStack stack,
            String name
    ) {
        stack.set(DataComponents.ITEM_NAME, Component.literal(name));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            Level pLevel,
            Player pPlayer,
            InteractionHand pUsedHand
    ) {
        var stack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            ClientStuff.showProgramEditScreen(
                    getProgram(stack),
                    programString -> PacketDistributor.sendToServer(new ServerboundDiskItemSetProgramPacket(
                            programString,
                            pUsedHand
                    ))
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> list,
            TooltipFlag detail
    ) {
        boolean showProgram = FMLEnvironment.dist.isClient() && ClientStuff.isMoreInfoKeyDown();
        if (!showProgram) {
            list.addAll(LabelPositionHolder.from(stack).asHoverText());
            getErrors(stack)
                    .stream()
                    .map(line -> line.copy().withStyle(ChatFormatting.RED))
                    .forEach(list::add);
            getWarnings(stack)
                    .stream()
                    .map(line -> line.copy().withStyle(ChatFormatting.YELLOW))
                    .forEach(list::add);
            list.add(Constants.LocalizationKeys.GUI_ADVANCED_TOOLTIP_HINT
                             .getComponent(SFMKeyMappings.MORE_INFO_TOOLTIP_KEY.get().getTranslatedKeyMessage())
                             .withStyle(ChatFormatting.AQUA));
        } else {
            var program = getProgram(stack);
            if (!program.isEmpty()) {
                var start = Component.empty();
                ChatFormatting[] rainbowColors = new ChatFormatting[]{
                        ChatFormatting.DARK_RED,
                        ChatFormatting.RED,
                        ChatFormatting.GOLD,
                        ChatFormatting.YELLOW,
                        ChatFormatting.DARK_GREEN,
                        ChatFormatting.GREEN,
                        ChatFormatting.DARK_AQUA,
                        ChatFormatting.AQUA,
                        ChatFormatting.DARK_BLUE,
                        ChatFormatting.BLUE,
                        ChatFormatting.DARK_PURPLE,
                        ChatFormatting.LIGHT_PURPLE
                };
                int rainbowColorsLength = rainbowColors.length;
                int fullCycleLength = 2 * rainbowColorsLength - 2;
                for (int i = 0; i < getName(stack).getString().length() - 2; i++) {
                    int cyclePosition = i % fullCycleLength;
                    int adjustedIndex = cyclePosition < rainbowColorsLength
                                        ? cyclePosition
                                        : fullCycleLength - cyclePosition;
                    ChatFormatting color = rainbowColors[adjustedIndex];
                    start = start.append(Component.literal("=").withStyle(color));
                }
                list.add(start);
                list.addAll(ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(program, false));
            }
        }
    }
}
