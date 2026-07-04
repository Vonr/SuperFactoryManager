package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.client.screen.SFMInputDiagnosticsScreen;
import ca.teamdman.sfm.client.screen.text_editor.ISFMTextEditScreen;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditScreenOpenContext;
import ca.teamdman.sfm.client.text_editor.SFMTextEditScreenTitleScreenOpenContext;
import ca.teamdman.sfm.common.event_bus.SFMSubscribeEvent;
import ca.teamdman.sfm.common.label.LabelPositionHolder;
import ca.teamdman.sfm.common.util.SFMDist;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;

public class TitleScreenOpenTextEditorOnLaunchHandler {
    public static final String TITLE_SCREEN_PROPERTY = "sfm.clientRun.titleScreen";
    public static final String OPEN_TEXT_EDITOR_ON_TITLE_SCREEN_PROPERTY = "sfm.clientRun.openTextEditorOnTitleScreen";
    public static final String TITLE_SCREEN_TEXT_EDITOR = "text-editor";
    public static final String TITLE_SCREEN_INPUT_DIAG = "input-diag";
    public static boolean firstTime = true;

    @SFMSubscribeEvent(value = SFMDist.CLIENT)
    public static void onTitleScreenOpen(ScreenEvent.Opening event) {
        String launchScreen = getTitleScreenLaunchScreen();
        if (launchScreen.isEmpty()) return;
        if (!firstTime) return;
        if (event.getNewScreen() instanceof TitleScreen titleScreen) {
            firstTime = false;
            switch (launchScreen) {
                case TITLE_SCREEN_TEXT_EDITOR -> event.setNewScreen(createTextEditorScreen(titleScreen).asScreen());
                case TITLE_SCREEN_INPUT_DIAG -> event.setNewScreen(new SFMInputDiagnosticsScreen(titleScreen));
                default -> throw new IllegalStateException("Unsupported SFM title screen launch screen: " + launchScreen);
            }
        }
    }

    private static String getTitleScreenLaunchScreen() {
        String launchScreen = System.getProperty(TITLE_SCREEN_PROPERTY, "").trim();
        if (!launchScreen.isEmpty()) {
            return launchScreen;
        }
        if (Boolean.getBoolean(OPEN_TEXT_EDITOR_ON_TITLE_SCREEN_PROPERTY)) {
            return TITLE_SCREEN_TEXT_EDITOR;
        }
        return "";
    }

    private static ISFMTextEditScreen createTextEditorScreen(TitleScreen titleScreen) {
        ISFMTextEditScreenOpenContext ctx = new SFMTextEditScreenTitleScreenOpenContext(
                "",
                LabelPositionHolder.empty(),
                s -> {},
                titleScreen
        );
        return SFMScreenChangeHelpers.createProgramEditScreen(ctx);
    }
}
