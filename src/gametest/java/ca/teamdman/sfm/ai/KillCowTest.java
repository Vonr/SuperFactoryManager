package ca.teamdman.sfm.ai;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.gametest.SFMGameTestBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class KillCowTest extends SFMGameTestBase {
    @GameTest(template = "3x4x3")
    public static void kill_cow(GameTestHelper helper) {
        var animalPos = new BlockPos(1, 2, 1);

        // surround it with fence
        for (int x = 0; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                helper.setBlock(new BlockPos(x, 2, z), Blocks.OAK_FENCE);
            }
        }
        // spawn the animal
        var cowType = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("minecraft", "cow"));

        // begin agent code
        // item.setPos(Vec3.atCenterOf(helper.absolutePos(pressurePlatePos)));
        // end agent code

        helper.spawn(cowType, new BlockPos(1, 2, 1));

        helper.succeedWhen(() -> {
            // make sure the cow is dead
            helper.assertEntityPresent(cowType, animalPos);
        });
    }
}
