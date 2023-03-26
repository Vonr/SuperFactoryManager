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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

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
        assertTwoChestTest(
                helper,
                """
                            EVERY 20 TICKS DO
                                INPUT FROM a
                                OUTPUT TO b
                            END
                        """,
                (left) -> left.insertItem(0, new ItemStack(Blocks.DIRT, 64), false),
                right -> {
                },
                left -> helper.assertTrue(left.getStackInSlot(0).isEmpty(), "Dirt did not move"),
                right -> helper.assertTrue(right.getStackInSlot(0).getCount() == 64, "Dirt did not move")
        );
    }

    @GameTest(template = "twochest")
    public static void MoveOne(GameTestHelper helper) {
        assertTwoChestTest(
                helper,
                """
                            EVERY 20 TICKS DO
                                INPUT 1 FROM a
                                OUTPUT TO b
                            END
                        """,
                (left) -> left.insertItem(0, new ItemStack(Blocks.DIRT, 64), false),
                right -> {
                },
                left -> helper.assertTrue(left.getStackInSlot(0).getCount() == 63, "Dirt did not move"),
                right -> helper.assertTrue(right.getStackInSlot(0).getCount() == 1, "Dirt did not move")
        );
    }

    @GameTest(template = "twochest")
    public static void MoveFull(GameTestHelper helper) {
        assertTwoChestTest(
                helper,
                """
                            EVERY 20 TICKS DO
                                INPUT FROM a
                                OUTPUT TO b
                            END
                        """,
                (left) -> fillWith(left, new ItemStack(Blocks.DIRT, 64)),
                right -> {
                },
                left -> helper.assertTrue(
                        IntStream
                                .range(0, left.getSlots())
                                .allMatch(slot -> left.getStackInSlot(slot).isEmpty()),
                        "Dirt did not leave"
                ),
                right -> helper.assertTrue(
                        countMatches(right, x -> x.is(Items.DIRT), right.getSlots() * 64),
                        "Dirt did not arrive"
                )
        );
    }

    @GameTest(template = "twochest")
    public static void RetainSome(GameTestHelper helper) {
        assertTwoChestTest(
                helper,
                """
                            EVERY 20 TICKS DO
                                INPUT RETAIN 5 FROM a
                                OUTPUT TO b
                            END
                        """,
                (left) -> left.insertItem(0, new ItemStack(Blocks.DIRT, 64), false),
                right -> {
                },
                left -> helper.assertTrue(left.getStackInSlot(0).getCount() == 5, "Dirt did not move"),
                right -> helper.assertTrue(right.getStackInSlot(0).getCount() == 64 - 5, "Dirt did not move")
        );
    }

    @GameTest(template = "twochest")
    public static void MultiInputOutput(GameTestHelper helper) {
        assertTwoChestTest(
                helper,
                """
                            EVERY 20 TICKS DO
                                INPUT
                                    RETAIN 5 iron_ingot,
                                    RETAIN 3 stone
                                FROM a TOP SIDE
                            
                                OUTPUT
                                    2 iron_ingot,
                                    RETAIN 10 stone
                                TO b
                            END
                        """,
                (left) -> {
                    left.insertItem(0, new ItemStack(Items.IRON_INGOT, 64), false);
                    left.insertItem(1, new ItemStack(Items.STONE, 64), false);
                },
                right -> {
                },
                left -> {
                    helper.assertTrue(left.getStackInSlot(0).getCount() == 64 - 2, "Iron ingots did not retain");
                    helper.assertTrue(left.getStackInSlot(1).getCount() == 64 - 10, "Stone did not retain");
                },
                right -> {
                    helper.assertTrue(right.getStackInSlot(0).getCount() == 2, "Iron ingots did not move");
                    helper.assertTrue(right.getStackInSlot(1).getCount() == 10, "Stone did not move");
                }
        );
    }

    @GameTest(template = "twochest")
    public static void MultiSlotInputOutput(GameTestHelper helper) {
        assertTwoChestTest(
                helper,
                """
                            EVERY 20 TICKS DO
                                INPUT FROM a TOP SIDE SLOTS 0,1,3-4,5
                                OUTPUT TO a SLOTS 2
                            END
                        """,
                (left) -> {
                    left.insertItem(0, new ItemStack(Items.DIAMOND, 5), false);
                    left.insertItem(1, new ItemStack(Items.DIAMOND, 5), false);
                    left.insertItem(3, new ItemStack(Items.DIAMOND, 5), false);
                    left.insertItem(4, new ItemStack(Items.DIAMOND, 5), false);
                    left.insertItem(5, new ItemStack(Items.DIAMOND, 5), false);
                },
                right -> {
                },
                left -> {
                    helper.assertTrue(left.getStackInSlot(0).isEmpty(), "slot 0 did not leave");
                    helper.assertTrue(left.getStackInSlot(1).isEmpty(), "slot 1 did not leave");
                    helper.assertTrue(left.getStackInSlot(3).isEmpty(), "slot 3 did not leave");
                    helper.assertTrue(left.getStackInSlot(4).isEmpty(), "slot 4 did not leave");
                    helper.assertTrue(left.getStackInSlot(5).isEmpty(), "slot 5 did not leave");
                    helper.assertTrue(left.getStackInSlot(2).getCount() == 25, "Items did not transfer to slot 2");
                },
                right -> {
                    helper.assertTrue(
                            IntStream
                                    .range(0, right.getSlots())
                                    .allMatch(slot -> right.getStackInSlot(slot).isEmpty()),
                            "Chest b is not empty"
                    );
                }
        );
    }


    private static void fillWith(IItemHandler inv, ItemStack stack) {
        for (int i = 0; i < inv.getSlots(); i++) {
            inv.insertItem(i, stack.copy(), false);
        }
    }

    private static boolean countMatches(IItemHandler inv, Predicate<ItemStack> filter, int count) {
        int total = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            if (filter.test(inv.getStackInSlot(i))) {
                total += inv.getStackInSlot(i).getCount();
            }
        }
        return total == count;
    }

    private static void assertTwoChestTest(
            GameTestHelper helper,
            String program,
            Consumer<IItemHandler> setupLeft,
            Consumer<IItemHandler> setupRight,
            Consumer<IItemHandler> leftCheck,
            Consumer<IItemHandler> rightCheck
    ) {
        helper.assertBlock(new BlockPos(1, 2, 0), ManagerBlock.class::isInstance, "Manager did not spawn");
        helper.assertBlock(new BlockPos(0, 2, 0), b -> b == Blocks.CHEST, "Chest did not spawn");
        helper.assertBlock(new BlockPos(2, 2, 0), b -> b == Blocks.CHEST, "Chest did not spawn");

        var right = (helper.getBlockEntity(new BlockPos(0, 2, 0)))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();
        var left  = (helper.getBlockEntity(new BlockPos(2, 2, 0)))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();

        setupLeft.accept(left);
        setupRight.accept(right);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setProgram(program);
        helper.assertTrue(manager.getState() == ManagerBlockEntity.State.RUNNING, "Program did not start running");

        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            leftCheck.accept(left);
            rightCheck.accept(right);
            helper.succeed();
        });
    }
}
