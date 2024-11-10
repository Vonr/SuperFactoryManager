package ca.teamdman.sfm.client.gui.screen;

public class ServerConfigEditScreen extends ProgramEditScreen {
    public ServerConfigEditScreen(
            String currentServerConfig
    ) {
        super(currentServerConfig, ServerConfigEditScreen::commitChanges);
    }

    private static void commitChanges(String newServerConfig) {
    }
}
