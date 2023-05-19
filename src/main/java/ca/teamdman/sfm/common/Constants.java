package ca.teamdman.sfm.common;

import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Constants {
    public static final class LocalizationKeys {
        public static final LocalizationEntry PROGRAM_EDIT_SCREEN_TITLE = new LocalizationEntry(
                "gui.sfm.text_editor.title",
                "Text Editor"
        );
        public static final LocalizationEntry PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP = new LocalizationEntry(
                "gui.sfm.text_editor.done_button.tooltip",
                "Shift+Enter to submit"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry MOD_NAME = new LocalizationEntry(
                "mod.name",
                "Super Factory Manager"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference


        public static LocalizationEntry ITEM_GROUP       = new LocalizationEntry(
                "item_group.sfm.main",
                "Super Factory Manager"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry CABLE_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.CABLE_BLOCK.get().getDescriptionId(),
                () -> "Inventory Cable"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry MANAGER_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.MANAGER_BLOCK.get().getDescriptionId(),
                () -> "Factory Manager"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry PRINTING_PRESS_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.PRINTING_PRESS_BLOCK.get().getDescriptionId(),
                () -> "Printing Press"
        );

        public static final LocalizationEntry PRINTING_PRESS_JEI_CATEGORY_TITLE = new LocalizationEntry(
                "gui.jei.category.sfm.printing_press",
                "Printing Press"
        );

        public static final LocalizationEntry FALLING_ANVIL_JEI_CATEGORY_TITLE = new LocalizationEntry(
                "gui.jei.category.sfm.falling_anvil",
                "Falling Anvil"
        );

        public static final LocalizationEntry FALLING_ANVIL_JEI_CONSUMED = new LocalizationEntry(
                "gui.jei.category.sfm.falling_anvil.consumed",
                "Gets consumed"
        );
        public static final LocalizationEntry FALLING_ANVIL_JEI_NOT_CONSUMED = new LocalizationEntry(
                "gui.jei.category.sfm.falling_anvil.not_consumed",
                "Not consumed"
        );

        public static final LocalizationEntry PRINTING_PRESS_TOOLTIP = new LocalizationEntry(
                () -> SFMItems.PRINTING_PRESS_ITEM.get().getDescriptionId() + ".tooltip",
                () -> "Place with an air gap below a downward facing piston. Extend the piston to use."
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry TEST_BARREL_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.TEST_BARREL_BLOCK.get().getDescriptionId(),
                () -> "Test Barrel"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry WATER_TANK_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.WATER_TANK_BLOCK.get().getDescriptionId(),
                () -> "Water Tank"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry BATTERY_BLOCK = new LocalizationEntry(
                () -> SFMBlocks.BATTERY_BLOCK.get().getDescriptionId(),
                () -> "Battery (WIP)"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry DISK_ITEM = new LocalizationEntry(
                () -> SFMItems.DISK_ITEM.get().getDescriptionId(),
                () -> "Factory Manager Program Disk"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry EXPERIENCE_GOOP_ITEM = new LocalizationEntry(
                () -> SFMItems.EXPERIENCE_GOOP_ITEM.get().getDescriptionId(),
                () -> "Experience Goop"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry EXPERIENCE_SHARD_ITEM = new LocalizationEntry(
                () -> SFMItems.EXPERIENCE_SHARD_ITEM.get().getDescriptionId(),
                () -> "Experience Shard"
        );

        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry FORM_ITEM = new LocalizationEntry(
                () -> SFMItems.FORM_ITEM.get().getDescriptionId(),
                () -> "Printing Form"
        );

        public static final LocalizationEntry DISK_ITEM_TOOLTIP_LABEL_HEADER = new LocalizationEntry(
                () -> SFMItems.DISK_ITEM.get().getDescriptionId() + ".tooltip.label_section.header",
                () -> "Labels"
        );
        public static final LocalizationEntry DISK_ITEM_TOOLTIP_LABEL = new LocalizationEntry(
                () -> SFMItems.DISK_ITEM.get().getDescriptionId() + ".tooltip.label_section.entry",
                () -> " - %s: %d blocks"
        );
        @SuppressWarnings("unused") // used by minecraft without us having to directly reference
        public static LocalizationEntry LABEL_GUN_ITEM = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId(),
                () -> "Label Gun"
        );
        public static final LocalizationEntry LABEL_GUN_CHAT_PULLED = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".chat.pulled",
                () -> "Pulled labels from the manager."
        );
        public static final LocalizationEntry LABEL_GUN_CHAT_PUSHED = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".chat.pushed",
                () -> "Pushed labels to the manager."
        );
        public static final LocalizationEntry LABEL_GUN_ITEM_TOOLTIP_1 = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".tooltip.1",
                () -> "Right click a Factory Manager to push labels."
        );
        public static final LocalizationEntry LABEL_GUN_ITEM_TOOLTIP_2 = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".tooltip.2",
                () -> "Right click a Factory Manager while sneaking to pull labels."
        );
        public static final LocalizationEntry LABEL_GUN_ITEM_NAME_WITH_LABEL = new LocalizationEntry(
                () -> SFMItems.LABEL_GUN_ITEM.get().getDescriptionId() + ".with_label",
                () -> "Label Gun: \"%s\""
        );
        public static final LocalizationEntry WATER_TANK_ITEM_TOOLTIP_1 = new LocalizationEntry(
                () -> SFMBlocks.WATER_TANK_BLOCK.get().getDescriptionId() + ".tooltip.1",
                () -> "Requires two adjacent water sources"
        );
        public static final LocalizationEntry WATER_TANK_ITEM_TOOLTIP_2 = new LocalizationEntry(
                () -> SFMBlocks.WATER_TANK_BLOCK.get().getDescriptionId() + ".tooltip.2",
                () -> "More effective when also adjacent to other water tanks"
        );
        public static final LocalizationEntry LABEL_GUN_GUI_TITLE = new LocalizationEntry(
                "gui.sfm.title.labelgun",
                "Label Gun"
        );
        public static final LocalizationEntry LABEL_GUN_GUI_LABEL_PLACEHOLDER = new LocalizationEntry(
                "gui.sfm.label_gun.placeholder",
                "Label"
        );
        public static final LocalizationEntry LABEL_GUN_GUI_LABEL_BUTTON = new LocalizationEntry(
                "gui.sfm.label_gun.label_button",
                "%s (%d)"
        );

        public static final LocalizationEntry LABEL_GUN_GUI_PRUNE_BUTTON = new LocalizationEntry(
                "gui.sfm.label_gun.prune_button",
                "Prune"
        );
        public static final LocalizationEntry LABEL_GUN_GUI_CLEAR_BUTTON = new LocalizationEntry(
                "gui.sfm.label_gun.clear_button",
                "Clear"
        );

        public static final LocalizationEntry MANAGER_GUI_PASTE_BUTTON_TOOLTIP = new LocalizationEntry(
                "gui.sfm.manager.tooltip.paste",
                "Press Ctrl+V to paste."
        );
        public static final LocalizationEntry MANAGER_GUI_EDIT_BUTTON_TOOLTIP = new LocalizationEntry(
                "gui.sfm.manager.edit_button.tooltip",
                "Press Ctrl+E to edit."
        );
        public static final LocalizationEntry MANAGER_GUI_EDIT_BUTTON = new LocalizationEntry(
                "gui.sfm.manager.edit_button",
                "Edit"
        );
        public static final LocalizationEntry MANAGER_RESET_BUTTON_TOOLTIP = new LocalizationEntry(
                "gui.sfm.manager.tooltip.reset",
                "Wipes ALL disk data."
        );
        public static final LocalizationEntry MANAGER_CONTAINER = new LocalizationEntry(
                "container.sfm.manager",
                "Factory Manager"
        );

        public static final LocalizationEntry PROGRAM_WARNING_UNUSED_LABEL = new LocalizationEntry(
                "program.sfm.warnings.unused_label",
                "Label \"%s\" is used in code but not assigned in the world."
        );
        public static final LocalizationEntry PROGRAM_WARNING_UNKNOWN_RESOURCE_TYPE = new LocalizationEntry(
                "program.sfm.warnings.unknown_resource_type",
                "The resource type \"%s\" in \"%s\" is not supported."
        );
        public static final LocalizationEntry PROGRAM_WARNING_UNKNOWN_RESOURCE_ID = new LocalizationEntry(
                "program.sfm.warnings.unknown_resource_id",
                "Resource \"%s\" was not found."
        );
        public static final LocalizationEntry PROGRAM_WARNING_UNDEFINED_LABEL = new LocalizationEntry(
                "program.sfm.warnings.undefined_label",
                "Label \"%s\" is assigned in the world but not defined in code."
        );
        public static final LocalizationEntry PROGRAM_WARNING_DISCONNECTED_LABEL = new LocalizationEntry(
                "program.sfm.warnings.disconnected_label",
                "Label \"%s\" is assigned in the world at %s but not connected by cables."
        );
        public static final LocalizationEntry PROGRAM_WARNING_ADJACENT_BUT_DISCONNECTED_LABEL = new LocalizationEntry(
                "program.sfm.warnings.adjacent_but_disconnected_label",
                "Label \"%s\" is assigned in the world at %s and is connected by cables but is not detected as a valid inventory."
        );
        public static final LocalizationEntry PROGRAM_ERROR_COMPILE_FAILED = new LocalizationEntry(
                "program.sfm.error.compile_failed",
                "Failed to compile."
        );
        public static final LocalizationEntry PROGRAM_ERROR_LITERAL = new LocalizationEntry(
                "program.sfm.error.literal",
                "%s"
        );
        public static final LocalizationEntry PROGRAM_ERROR_MALFORMED_RESOURCE_TYPE = new LocalizationEntry(
                "program.sfm.error.malformed_resource_type",
                "Program has a malformed resource type \"%s\".\nReminder: Resource types must be literals, not wildcards."
        );
        public static final LocalizationEntry MANAGER_GUI_STATE_NO_PROGRAM = new LocalizationEntry(
                "gui.sfm.manager.state.no_program",
                "no program"
        );
        public static final LocalizationEntry MANAGER_GUI_STATE = new LocalizationEntry(
                "gui.sfm.manager.state",
                "State: %s"
        );
        public static final LocalizationEntry MANAGER_GUI_PEAK_TICK_TIME = new LocalizationEntry(
                "gui.sfm.manager.peak_tick_time",
                "Peak tick time: %s ns"
        );
        public static final LocalizationEntry MANAGER_GUI_HOVERED_TICK_TIME = new LocalizationEntry(
                "gui.sfm.manager.hovered_tick_time",
                "Hovered tick time: %s ns"
        );
        public static final LocalizationEntry MANAGER_GUI_STATE_NO_DISK = new LocalizationEntry(
                "gui.sfm.manager.state.no_disk",
                "missing disk"
        );
        public static final LocalizationEntry MANAGER_GUI_STATE_RUNNING = new LocalizationEntry(
                "gui.sfm.manager.state.running",
                "running"
        );
        public static final LocalizationEntry MANAGER_GUI_STATE_INVALID_PROGRAM = new LocalizationEntry(
                "gui.sfm.manager.state.invalid_program",
                "invalid program"
        );
        public static final LocalizationEntry MANAGER_GUI_BUTTON_IMPORT_CLIPBOARD = new LocalizationEntry(
                "gui.sfm.manager.button.import_clipboard",
                "Import clipboard"
        );
        public static final LocalizationEntry MANAGER_GUI_BUTTON_EXPORT_CLIPBOARD = new LocalizationEntry(
                "gui.sfm.manager.button.export_clipboard",
                "Export clipboard"
        );
        public static final LocalizationEntry MANAGER_GUI_BUTTON_RESET = new LocalizationEntry(
                "gui.sfm.manager.button.reset",
                "Reset"
        );

        public static final LocalizationEntry MANAGER_GUI_WARNING_BUTTON_TOOLTIP = new LocalizationEntry(
                "gui.sfm.manager.button.warning.tooltip",
                "Click to copy code with warnings and errors.\nShift-click to attempt to fix warnings."
        );

        public static final LocalizationEntry MANAGER_GUI_WARNING_BUTTON_TOOLTIP_READ_ONLY = new LocalizationEntry(
                "gui.sfm.manager.button.warning.tooltip.read_only",
                "Click to copy code with warnings and errors."
        );

        public static final LocalizationEntry MANAGER_GUI_STATUS_LOADED_CLIPBOARD = new LocalizationEntry(
                "gui.sfm.manager.status.loaded_clipboard",
                "Loaded from clipboard!"
        );
        public static final LocalizationEntry MANAGER_GUI_STATUS_SAVED_CLIPBOARD = new LocalizationEntry(
                "gui.sfm.manager.status.saved_clipboard",
                "Saved to clipboard!"
        );
        public static final LocalizationEntry MANAGER_GUI_STATUS_RESET = new LocalizationEntry(
                "gui.sfm.manager.status.reset",
                "Reset program and labels!"
        );
        public static final LocalizationEntry MANAGER_GUI_STATUS_FIX = new LocalizationEntry(
                "gui.sfm.manager.status.fix",
                "Cleaning up labels!"
        );

        public static final LocalizationEntry GUI_ADVANCED_TOOLTIP_HINT = new LocalizationEntry(
                "gui.sfm.advanced.tooltip.hint",
                ChatFormatting.GRAY + "Hold " + ChatFormatting.AQUA + "%s" + ChatFormatting.GRAY + " for more info"
        );

        public static final LocalizationEntry KEY_MORE_INFO = new LocalizationEntry(
                "key.sfm.more_info",
                "Press for more info key"
        );


        public static final LocalizationEntry SFM_KEY_CATEGORY = new LocalizationEntry(
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

            public TranslatableContents get(Object... args) {
                return new TranslatableContents(key.get(), null, args);
            }

            public TranslatableContents get() {
                return new TranslatableContents(key.get(), null, new Object[]{});
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
