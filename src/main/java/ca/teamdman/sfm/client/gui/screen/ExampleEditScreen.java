package ca.teamdman.sfm.client.gui.screen;

import net.minecraft.client.gui.screens.ConfirmScreen;

import java.util.Map;
import java.util.function.Consumer;

public class ExampleEditScreen extends ProgramEditScreen {
    private final Map<String, String> templates;
    private final String program;

    public ExampleEditScreen(
            String program,
            String initialContent,
            Map<String, String> templates,
            Consumer<String> saveCallback
    ) {
        super(initialContent, saveCallback);
        this.program = program;
        this.templates = templates;
    }

    public boolean equalsAnyTemplate(String content) {
        return templates.values().stream().anyMatch(content::equals);
    }

    @Override
    public void saveAndClose() {
        // The user is attempting to apply a code change to the disk
        if (equalsAnyTemplate(program)) {
            // The disk contains template code, safe to overwrite
            super.saveAndClose();
        } else {
            // The disk contains non-template code, ask before overwriting
            assert this.minecraft != null;
            ConfirmScreen saveConfirmScreen = getSaveConfirmScreen(super::saveAndClose);
            this.minecraft.pushGuiLayer(saveConfirmScreen);
            saveConfirmScreen.setDelay(20);
        }
    }

    @Override
    public void onClose() {
        // The user has requested to close the screen
        // If the content has changed, ask to save before discarding
        if (!equalsAnyTemplate(textarea.getValue())) {
            assert this.minecraft != null;
            ConfirmScreen exitWithoutSavingConfirmScreen = getExitWithoutSavingConfirmScreen();
            this.minecraft.pushGuiLayer(exitWithoutSavingConfirmScreen);
            exitWithoutSavingConfirmScreen.setDelay(20);
        } else {
            super.onClose();
        }
    }
}
