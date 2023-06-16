package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfml.ast.Program;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProgramTemplatePickerScreen extends Screen {
    private final Consumer<String> CALLBACK;

    public ProgramTemplatePickerScreen(Consumer<String> callback) {
        super(Constants.LocalizationKeys.PROGRAM_TEMPLATE_PICKER_GUI_TITLE.getComponent());
        CALLBACK = callback;
    }

    @Override
    protected void init() {
        super.init();

        //discover template programs
        var irm = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> found = irm.listResources(
                "template_programs",
                (path) -> path.getPath().endsWith(".sfml") || path.getPath().endsWith(".sfm")
        );
        Map<String, String> templatePrograms = new HashMap<>();
        // sort alphabetically by name (key)
        for (var entry : found.entrySet()) {
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                String program = reader.lines().collect(Collectors.joining("\n"));
                Program.compile(
                        program,
                        success -> templatePrograms.put(
                                success.name().isBlank() ? entry.getKey().toString() : success.name(),
                                program
                        ),
                        failure -> templatePrograms.put(entry.getKey().toString(), program)
                );
            } catch (IOException ignored) {
            }
        }

        // add picker buttons
        {
            int i = 0;
            int buttonWidth = templatePrograms.keySet()
                                      .stream()
                                      .mapToInt(this.font::width)
                                      .max().orElse(50) + 10;
            int buttonHeight = 20;
            int paddingX = 5;
            int paddingY = 5;
            int buttonsPerRow = this.width / (buttonWidth + paddingX);
            for (var entry : templatePrograms
                    .entrySet()
                    .stream()
                    .sorted((o1, o2) -> Comparator.<String>naturalOrder().compare(o1.getKey(), o2.getKey()))
                    .toList()) {
                int x = (this.width - (buttonWidth + paddingX) * Math.min(buttonsPerRow, templatePrograms.size())) / 2
                        + paddingX
                        + (i % buttonsPerRow) * (
                        buttonWidth
                        + paddingX
                );
                int y = 50 + (i / buttonsPerRow) * (buttonHeight + paddingY);
                this.addRenderableWidget(new Button(
                        x,
                        y,
                        buttonWidth,
                        buttonHeight,
                        Component.literal(entry.getKey()),
                        (btn) -> {
                            onClose();
                            CALLBACK.accept(entry.getValue());
                        }
                ));
                i++;
            }
        }
    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBackground(pPoseStack);
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        MutableComponent warning1 = Constants.LocalizationKeys.PROGRAM_TEMPLATE_PICKER_GUI_WARNING_1.getComponent();
        this.font.draw(
                pPoseStack,
                warning1,
                this.width / 2f - this.font.width(warning1) / 2f,
                20,
                16777215
        );
        MutableComponent warning2 = Constants.LocalizationKeys.PROGRAM_TEMPLATE_PICKER_GUI_WARNING_2.getComponent();
        this.font.draw(
                pPoseStack,
                warning2,
                this.width / 2f - this.font.width(warning2) / 2f,
                36,
                16777215
        );
    }
}
