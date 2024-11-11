package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.SFM;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigExporter {
    public static String getConfigToml(ForgeConfigSpec configSpec) {
        Config childConfig;
        try {
            Field childConfigField = configSpec.getClass().getDeclaredField("childConfig");
            childConfigField.setAccessible(true);
            childConfig = (Config) childConfigField.get(configSpec);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SFM.LOGGER.error("Failed to extract childConfig field", e);
            return "(failed to gather config content)";
        }
        if (!(childConfig instanceof FileConfig fileConfig)) {
            SFM.LOGGER.error("Failed to extract config path");
            return "(failed to gather config content)";
        }
        Path configPath = fileConfig.getNioPath();
        try {
            return Files.readString(configPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            SFM.LOGGER.error("Failed reading config contents", e);
            return "(failed to gather config content)";
        }
    }
}
