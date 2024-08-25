package ca.teamdman.sfm.common.localization;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMTestBlocks;

import java.util.ArrayList;
import java.util.List;

public final class TestLocalizationKeys {

    @SuppressWarnings("unused") // used by minecraft without us having to directly reference
    public static LocalizationEntry TEST_BARREL_BLOCK = new LocalizationEntry(
            () -> SFMTestBlocks.TEST_BARREL_BLOCK.get().getDescriptionId(),
            () -> "Test Barrel"
    );

    @SuppressWarnings("unused") // used by minecraft without us having to directly reference
    public static LocalizationEntry BATTERY_BLOCK = new LocalizationEntry(
            () -> SFMTestBlocks.BATTERY_BLOCK.get().getDescriptionId(),
            () -> "Battery (WIP)"
    );

    public static List<LocalizationEntry> getEntries() {
        // use reflection to get all the public static LocalizationEntry fields
        var rtn = new ArrayList<LocalizationEntry>();
        for (var field : TestLocalizationKeys.class.getFields()) {
            if (field.getType() == LocalizationEntry.class) {
                try {
                    rtn.add((LocalizationEntry) field.get(null));
                } catch (IllegalAccessException e) {
                    SFM.LOGGER.error("Failed reading entry field", e);
                }
            }
        }
        return rtn;
    }
}
