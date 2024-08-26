package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SFMLanguageProvider extends LanguageProvider {
    public SFMLanguageProvider(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), SFM.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        Set<String> seen = new HashSet<>();
        for (var entry : LocalizationKeys.getEntries()) {
            add(entry.key().get(), entry.value().get());
            seen.add(entry.key().get());
        }
        List<String> unmapped = new ArrayList<>();
        BuiltInRegistries.ITEM
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().location().getNamespace().equals(SFM.MOD_ID))
                .filter(entry -> !seen.contains(entry.getValue().getDescriptionId()))
                .map(entry -> entry.getValue().toString())
                .forEach(unmapped::add);
        BuiltInRegistries.BLOCK
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().location().getNamespace().equals(SFM.MOD_ID))
                .filter(entry -> !seen.contains(entry.getValue().getDescriptionId()))
                .map(entry -> entry.getValue().toString())
                .forEach(unmapped::add);
        if (!unmapped.isEmpty()) {
            throw new IllegalStateException("Unmapped entries: " + String.join(", ", unmapped));
        }
    }
}
