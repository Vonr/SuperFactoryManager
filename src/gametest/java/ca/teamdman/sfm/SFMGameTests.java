package ca.teamdman.sfm;

import ca.teamdman.sfm.common.block.ManagerBlock;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

// https://github.dev/CompactMods/CompactMachines
// https://github.com/SocketMods/BaseDefense/blob/3b3cb4af26f4553c3438417cbb95f0d3fb707751/build.gradle#L74
// https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/build.gradle#L54
// https://github.com/mekanism/Mekanism/blob/1.16.x/build.gradle
// https://github.com/TwistedGate/ImmersivePetroleum/blob/1.16.5/build.gradle#L107
// https://github.com/MinecraftForge/MinecraftForge/blob/d7b137d1446377bfd1958f8a0e24f63819b81bfc/src/test/java/net/minecraftforge/debug/misc/GameTestTest.java#L155
// https://docs.minecraftforge.net/en/1.19.x/misc/gametest/
// https://github.com/MinecraftForge/MinecraftForge/blob/1.19.x/src/test/java/net/minecraftforge/debug/misc/GameTestTest.java#LL101-L116C6

@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMGameTests {
    @GameTest(template = "single")
    public static void ManagerUpdatesState(GameTestHelper helper) {
        helper.setBlock(new BlockPos(0, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(0, 2, 0));
        helper.assertTrue(manager.getState() == ManagerBlockEntity.State.NO_DISK, "Manager did not start with no disk");
        helper.assertTrue(manager.getDisk().isEmpty(), "Manager did not start with no disk");
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        helper.assertTrue(
                manager.getState() == ManagerBlockEntity.State.NO_PROGRAM,
                "Disk did not start with no program"
        );
        manager.setProgram("""
                                       EVERY 20 TICKS DO
                                           INPUT FROM a
                                           OUTPUT TO b
                                       END
                                   """);
        helper.assertTrue(manager.getState() == ManagerBlockEntity.State.RUNNING, "Program did not start running");
        helper.succeed();
    }

    @GameTest(template = "twochest")
    public static void MoveAll(GameTestHelper helper) {
        helper.assertBlock(new BlockPos(1, 2, 0), ManagerBlock.class::isInstance, "Manager did not spawn");
        helper.assertBlock(new BlockPos(0, 2, 0), b -> b == Blocks.CHEST, "Chest did not spawn");
        helper.assertBlock(new BlockPos(2, 2, 0), b -> b == Blocks.CHEST, "Chest did not spawn");
        var program = """
                    EVERY 20 TICKS DO
                        INPUT FROM a
                        OUTPUT TO b
                    END
                """;
        ((ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0))).setProgram(program);
        var right = ((ChestBlockEntity) helper.getBlockEntity(new BlockPos(0, 2, 0)));
        var left  = ((ChestBlockEntity) helper.getBlockEntity(new BlockPos(2, 2, 0)));
        left.setItem(0, new ItemStack(Blocks.DIRT, 64));
        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            helper.assertTrue(right.getItem(0).getItem() == Items.DIRT, "Dirt did not move");
            helper.assertTrue(right.getItem(0).getCount() == 64, "Dirt did not move");
            helper.assertTrue(left.getItem(0).isEmpty(), "Dirt did not move");
            helper.succeed();
        });
    }
}
