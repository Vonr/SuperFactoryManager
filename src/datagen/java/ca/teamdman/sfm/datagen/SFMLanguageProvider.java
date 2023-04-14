package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class SFMLanguageProvider extends LanguageProvider {
    public SFMLanguageProvider(DataGenerator gen) {
        super(gen.getPackOutput(), SFM.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var entry : Constants.LocalizationKeys.getEntries()) {
            add(entry.key().get(), entry.value().get());
        }
    }
}
