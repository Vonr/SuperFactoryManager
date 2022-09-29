package ca.teamdman.sfm;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

// https://github.dev/CompactMods/CompactMachines
// https://github.com/SocketMods/BaseDefense/blob/3b3cb4af26f4553c3438417cbb95f0d3fb707751/build.gradle#L74
// https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/build.gradle#L54
// https://github.com/mekanism/Mekanism/blob/1.16.x/build.gradle
// https://github.com/TwistedGate/ImmersivePetroleum/blob/1.16.5/build.gradle#L107
@PrefixGameTestTemplate(false)
@GameTestHolder(SFM.MOD_ID)
public class SFMGameTests {
    //        @GameTest(templateNamespace = SFM.MOD_ID, template = "myfirststructure")
    @GameTest(template = "myfirststructure")
    public static void exampleTest(GameTestHelper helper) {
//        helper.
        System.out.println("hi");
        helper.succeed();
    }
}
