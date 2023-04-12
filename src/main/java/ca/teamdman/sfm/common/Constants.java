package ca.teamdman.sfm.common;

import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Constants {
    public static final class LocalizationKeys {
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry MOD_NAME         = new LocalizationEntry(
                "mod.name",
                "Super Factory Manager"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference


        public static LocalizationEntry ITEM_GROUP       = new LocalizationEntry(
                "itemGroup.sfm",
                "Super Factory Manager"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference

        public static LocalizationEntry CABLE_BLOCK      = new LocalizationEntry(
                () -> SFMBlocks.CABLE_BLOCK.get().getDescriptionId(),
                () -> "Inventory Cable"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference

        public static LocalizationEntry MANAGER_BLOCK    = new LocalizationEntry(
                () -> SFMBlocks.MANAGER_BLOCK.get().getDescriptionId(),
                () -> "Factory Manager"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference

        public static LocalizationEntry WATER_TANK_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.WATER_TANK_BLOCK.get().getDescriptionId(),
                () -> "Water Tank"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference

        public static LocalizationEntry DISK_ITEM        = new LocalizationEntry(
                () -> SFMItems.DISK_ITEM.get().getDescriptionId(),
                () -> "Factory Manager Program Disk"
        );

        public static LocalizationEntry DISK_ITEM_TOOLTIP_LABEL_HEADER                  = new LocalizationEntry(
                () -> SFMItems.DISK_ITEM.get().getDescriptionId() + ".tooltip.label.header",
                () -> "Labels"
        );
        public static LocalizationEntry DISK_ITEM_TOOLTIP_LABEL                         = new LocalizationEntry(
                () -> SFMItems.DISK_ITEM.get().getDescriptionId() + ".tooltip.label",
                () -> " - %s: %d blocks"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry LABEL_GUN_ITEM                                  = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId(),
                () -> "Label Gun"
        );
        public static LocalizationEntry LABEL_GUN_CHAT_PULLED                           = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".chat.pulled",
                () -> "Pulled labels from the manager."
        );
        public static LocalizationEntry LABEL_GUN_CHAT_PUSHED                           = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".chat.pushed",
                () -> "Pushed labels to the manager."
        );
        public static LocalizationEntry LABEL_GUN_ITEM_TOOLTIP_1                        = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".tooltip.1",
                () -> "Right click a Factory Manager to push labels."
        );
        public static LocalizationEntry LABEL_GUN_ITEM_TOOLTIP_2                        = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".tooltip.2",
                () -> "Right click a Factory Manager while sneaking to pull labels."
        );
        public static LocalizationEntry LABEL_GUN_ITEM_NAME_WITH_LABEL                  = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".with_label",
                () -> "Label Gun: \"%s\""
        );
        public static LocalizationEntry WATER_TANK_ITEM_TOOLTIP_1                       = new LocalizationEntry(
                () -> SFMBlocks.WATER_TANK_BLOCK.get().getDescriptionId() + ".tooltip.1",
                () -> "Requires two adjacent water sources"
        );
        public static LocalizationEntry WATER_TANK_ITEM_TOOLTIP_2                       = new LocalizationEntry(
                () -> SFMBlocks.WATER_TANK_BLOCK.get().getDescriptionId() + ".tooltip.2",
                () -> "More effective when also adjacent to other water tanks"
        );
        public static LocalizationEntry LABEL_GUN_GUI_TITLE                             = new LocalizationEntry(
                "gui.sfm.title.labelgun",
                "Label Gun"
        );
        public static LocalizationEntry LABEL_GUN_GUI_LABEL_PLACEHOLDER                 = new LocalizationEntry(
                "gui.sfm.label.labelgun.placeholder",
                "Label"
        );
        public static LocalizationEntry MANAGER_GUI_PASTE_BUTTON_TOOLTIP                = new LocalizationEntry(
                "gui.sfm.manager.tooltip.paste",
                "Press Ctrl+V to paste."
        );
        public static LocalizationEntry MANAGER_RESET_BUTTON_TOOLTIP                    = new LocalizationEntry(
                "gui.sfm.manager.tooltip.reset",
                "Wipes program AND label data."
        );
        public static LocalizationEntry MANAGER_CONTAINER                               = new LocalizationEntry(
                "container.sfm.manager",
                "Factory Manager"
        );
        public static LocalizationEntry PROGRAM_WARNING_UNUSED_LABEL                    = new LocalizationEntry(
                "program.sfm.warnings.unused_label",
                "Label \"%s\" is used in code but not assigned in the world."
        );
        public static LocalizationEntry PROGRAM_WARNING_UNKNOWN_RESOURCE_TYPE           = new LocalizationEntry(
                "program.sfm.warnings.unknown_resource_type",
                "The resource type \"%s\" in \"%s\" is not supported."
        );
        public static LocalizationEntry PROGRAM_WARNING_UNKNOWN_RESOURCE_ID             = new LocalizationEntry(
                "program.sfm.warnings.unknown_resource_id",
                "Resource \"%s\" was not found."
        );
        public static LocalizationEntry PROGRAM_WARNING_UNDEFINED_LABEL                 = new LocalizationEntry(
                "program.sfm.warnings.undefined_label",
                "Label \"%s\" is assigned in the world but not defined in code."
        );
        public static LocalizationEntry PROGRAM_WARNING_DISCONNECTED_LABEL              = new LocalizationEntry(
                "program.sfm.warnings.disconnected_label",
                "Label \"%s\" is assigned in the world at %s but not connected by cables."
        );
        public static LocalizationEntry PROGRAM_WARNING_ADJACENT_BUT_DISCONNECTED_LABEL = new LocalizationEntry(
                "program.sfm.warnings.adjacent_but_disconnected_label",
                "Label \"%s\" is assigned in the world at %s and is connected by cables but is not detected as a valid inventory."
        );
        public static LocalizationEntry PROGRAM_ERROR_COMPILE_FAILED                    = new LocalizationEntry(
                "program.sfm.error.compile_failed",
                "Failed to compile."
        );
        public static LocalizationEntry PROGRAM_ERROR_LITERAL                           = new LocalizationEntry(
                "program.sfm.error.literal",
                "%s"
        );
        public static LocalizationEntry PROGRAM_ERROR_MALFORMED_RESOURCE_TYPE           = new LocalizationEntry(
                "program.sfm.error.malformed_resource_type",
                "Program has a malformed resource type \"%s\".\nReminder: Resource types must be literals, not wildcards."
        );
        public static LocalizationEntry MANAGER_GUI_STATE_NO_PROGRAM                    = new LocalizationEntry(
                "gui.sfm.manager.state.no_program",
                "no program"
        );
        public static LocalizationEntry MANAGER_GUI_STATE                               = new LocalizationEntry(
                "gui.sfm.manager.state",
                "State: %s"
        );
        public static LocalizationEntry MANAGER_GUI_PEAK_TICK_TIME            = new LocalizationEntry(
                "gui.sfm.manager.peak_tick_time",
                "Peak tick time: %s us"
        );
        public static LocalizationEntry MANAGER_GUI_STATE_NO_DISK             = new LocalizationEntry(
                "gui.sfm.manager.state.no_disk",
                "missing disk"
        );
        public static LocalizationEntry MANAGER_GUI_STATE_RUNNING                       = new LocalizationEntry(
                "gui.sfm.manager.state.running",
                "running"
        );
        public static LocalizationEntry MANAGER_GUI_STATE_INVALID_PROGRAM               = new LocalizationEntry(
                "gui.sfm.manager.state.invalid_program",
                "invalid program"
        );
        public static LocalizationEntry MANAGER_GUI_BUTTON_IMPORT_CLIPBOARD             = new LocalizationEntry(
                "gui.sfm.manager.button.import_clipboard",
                "Import clipboard"
        );
        public static LocalizationEntry MANAGER_GUI_BUTTON_EXPORT_CLIPBOARD             = new LocalizationEntry(
                "gui.sfm.manager.button.export_clipboard",
                "Export clipboard"
        );
        public static LocalizationEntry MANAGER_GUI_BUTTON_RESET                        = new LocalizationEntry(
                "gui.sfm.manager.button.reset",
                "Reset"
        );
        public static LocalizationEntry MANAGER_GUI_WARNING_BUTTON_TOOLTIP              = new LocalizationEntry(
                "gui.sfm.manager.button.warning.tooltip",
                "Click to copy code with warnings and errors.\nShift-click to attempt to fix warnings."
        );
        public static LocalizationEntry MANAGER_GUI_STATUS_LOADED_CLIPBOARD             = new LocalizationEntry(
                "gui.sfm.manager.status.loaded_clipboard",
                "Loaded from clipboard!"
        );
        public static LocalizationEntry MANAGER_GUI_STATUS_SAVED_CLIPBOARD              = new LocalizationEntry(
                "gui.sfm.manager.status.saved_clipboard",
                "Saved to clipboard!"
        );
        public static LocalizationEntry MANAGER_GUI_STATUS_RESET                        = new LocalizationEntry(
                "gui.sfm.manager.status.reset",
                "Reset program and labels!"
        );
        public static LocalizationEntry MANAGER_GUI_STATUS_FIX                          = new LocalizationEntry(
                "gui.sfm.manager.status.fix",
                "Cleaning up labels!"
        );

        public static LocalizationEntry GUI_ADVANCED_TOOLTIP_HINT = new LocalizationEntry(
                "gui.sfm.advanced.tooltip.hint",
                ChatFormatting.GRAY + "Hold " + ChatFormatting.AQUA + "Shift" + ChatFormatting.GRAY + " for more info"
        );

        public static LocalizationEntry KEY_MORE_INFO = new LocalizationEntry(
                "key.sfm.more_info",
                "Press for more info key"
        );


        public static LocalizationEntry SFM_KEY_CATEGORY = new LocalizationEntry(
                "key.categories.sfm",
                "Super Factory Manager"
        );

        public static List<LocalizationEntry> getEntries() {
            // use reflection to get all the public static LocalizationEntry fields
            var rtn = new ArrayList<LocalizationEntry>();
            for (var field : Constants.LocalizationKeys.class.getFields()) {
                if (field.getType() == LocalizationEntry.class) {
                    try {
                        rtn.add((LocalizationEntry) field.get(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return rtn;
        }

        public record LocalizationEntry(
                Supplier<String> key,
                Supplier<String> value
        ) {
            public LocalizationEntry(String key, String value) {
                this(() -> key, () -> value);
            }

            public String getString() {
                return I18n.get(key.get());
            }

            public String getString(Object... args) {
                return I18n.get(key.get(), args);
            }

            public TranslatableContents get(Object... args) {
                return new TranslatableContents(key.get(), args);
            }

            public TranslatableContents get() {
                return new TranslatableContents(key.get());
            }

            public MutableComponent getComponent() {
                return Component.translatable(key.get());
            }

            public MutableComponent getComponent(Object... args) {
                return Component.translatable(key.get(), args);
            }
        }
    }
}
