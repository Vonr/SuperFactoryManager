package ca.teamdman.sfm;

import ca.teamdman.sfm.common.block.ManagerBlock;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.ast.Trigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new GameTestAssertException(message);
        }
    }

    @GameTest(template = "single")
    public static void ManagerUpdatesState(GameTestHelper helper) {
        helper.setBlock(new BlockPos(0, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(0, 2, 0));
        assertTrue(manager.getState() == ManagerBlockEntity.State.NO_DISK, "Manager did not start with no disk");
        assertTrue(manager.getDisk().isEmpty(), "Manager did not start with no disk");
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        assertTrue(manager.getState() == ManagerBlockEntity.State.NO_PROGRAM, "Disk did not start with no program");
        manager.setProgram("""
                                       EVERY 20 TICKS DO
                                           INPUT FROM a
                                           OUTPUT TO b
                                       END
                                   """);
        assertManagerRunning(manager);
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
                left -> assertTrue(left.getStackInSlot(0).isEmpty(), "Dirt did not move"),
                right -> assertTrue(right.getStackInSlot(0).getCount() == 64, "Dirt did not move")
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
                left -> assertTrue(left.getStackInSlot(0).getCount() == 63, "Dirt did not move"),
                right -> assertTrue(right.getStackInSlot(0).getCount() == 1, "Dirt did not move")
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
                left -> assertTrue(
                        IntStream
                                .range(0, left.getSlots())
                                .allMatch(slot -> left.getStackInSlot(slot).isEmpty()),
                        "Dirt did not leave"
                ),
                right -> assertTrue(
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
                left -> assertTrue(left.getStackInSlot(0).getCount() == 5, "Dirt did not move"),
                right -> assertTrue(right.getStackInSlot(0).getCount() == 64 - 5, "Dirt did not move")
        );
    }

    @GameTest(template = "twochest")
    public static void MultiInputOutput(GameTestHelper helper) {
        assertTwoChestTest(helper, """
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
                """, (left) -> {
            left.insertItem(0, new ItemStack(Items.IRON_INGOT, 64), false);
            left.insertItem(1, new ItemStack(Items.STONE, 64), false);
        }, right -> {
        }, left -> {
            assertTrue(left.getStackInSlot(0).getCount() == 64 - 2, "Iron ingots did not retain");
            assertTrue(left.getStackInSlot(1).getCount() == 64 - 10, "Stone did not retain");
        }, right -> {
            assertTrue(right.getStackInSlot(0).getCount() == 2, "Iron ingots did not move");
            assertTrue(right.getStackInSlot(1).getCount() == 10, "Stone did not move");
        });
    }

    @GameTest(template = "3x2x1") // start with empty platform
    public static void CauldronWaterMovement(GameTestHelper helper) {
        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos left = new BlockPos(2, 2, 0);
        helper.setBlock(left, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
        BlockPos right = new BlockPos(0, 2, 0);
        helper.setBlock(right, Blocks.CAULDRON);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "cauldron water test"
                                    
                    EVERY 20 TICKS DO
                        INPUT fluid:minecraft:water FROM a
                        OUTPUT fluid:*:* TO b
                    END
                """;

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(left));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(right));

        // load the program
        manager.setProgram(program);

        assertManagerRunning(manager);
        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            helper.assertBlock(left, b -> b == Blocks.CAULDRON, "cauldron didn't empty");
            helper.assertBlockState(
                    right,
                    s -> s.getBlock() == Blocks.WATER_CAULDRON
                         && s.getValue(LayeredCauldronBlock.LEVEL) == 3,
                    () -> "cauldron didn't fill"
            );
            helper.succeed();
        });
    }

    @GameTest(template = "25x3x25")
    public static void LongCableMovement(GameTestHelper helper) {
        BlockPos start = new BlockPos(0, 2, 0);
        BlockPos end   = new BlockPos(12, 2, 12);

        var len     = 24;
        var dir     = Direction.EAST;
        var current = start;
        while (len > 0) {
            // fill len blocks
            for (int i = 0; i < len; i++) {
                helper.setBlock(current, SFMBlocks.CABLE_BLOCK.get());
                current = current.relative(dir);
            }
            // turn right
            dir = dir.getClockWise();
            len -= 1;
        }

        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        helper.setBlock(start, Blocks.CHEST);
        helper.setBlock(end, Blocks.CHEST);

        // add some items
        ChestBlockEntity startChest = (ChestBlockEntity) helper.getBlockEntity(start);
        startChest.setItem(0, new ItemStack(Items.IRON_INGOT, 64));
        ChestBlockEntity endChest = (ChestBlockEntity) helper.getBlockEntity(end);


        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "long cable test"
                                    
                    EVERY 20 TICKS DO
                        INPUT FROM a
                        OUTPUT TO b
                    END
                """;

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(start));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(end));

        // load the program
        manager.setProgram(program);

        assertManagerRunning(manager);
        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            // ensure item arrived
            assertTrue(endChest.getItem(0).getCount() == 64, "Items did not move");
            // ensure item left
            assertTrue(startChest.getItem(0).isEmpty(), "Items did not leave");
            helper.succeed();
        });
    }


    @GameTest(template = "myfirststructure")
    public static void TestWaterMixingCrash(GameTestHelper helper) {
        // fill in the blocks needed for the test
        BlockPos managerPos = new BlockPos(1, 2, 1);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());

        BlockPos left = new BlockPos(2, 2, 1);
        helper.setBlock(left, Blocks.CHEST);
        // add sticks to the chest
        ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(left);
        chest.setItem(0, new ItemStack(Items.STICK, 64));

        BlockPos right = new BlockPos(0, 2, 1);
        helper.setBlock(right, Blocks.CHEST);

        BlockPos front = new BlockPos(1, 2, 2);
        helper.setBlock(front, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));

        BlockPos back = new BlockPos(1, 2, 0);
        helper.setBlock(back, Blocks.CAULDRON);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "water crash test"
                                    
                    every 20 ticks do
                        INPUT  item:minecraft:stick, fluid:minecraft:water FROM a
                        OUTPUT stick, fluid:minecraft:water TO b
                    end
                """;

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(left));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(front));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(right));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(back));

        // load the program
        manager.setProgram(program);

        assertManagerRunning(manager);
        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            helper.assertBlock(front, b -> b == Blocks.CAULDRON, "cauldron didn't empty");
            helper.assertBlockState(
                    back,
                    s -> s.getBlock() == Blocks.WATER_CAULDRON
                         && s.getValue(LayeredCauldronBlock.LEVEL) == 3,
                    () -> "cauldron didn't fill"
            );
            // ensure sticks departed
            assertTrue(chest.getItem(0).getCount() == 0, "Items did not move");
            // ensure sticks arrived
            ChestBlockEntity rightChest = (ChestBlockEntity) helper.getBlockEntity(right);
            assertTrue(rightChest.getItem(0).getCount() == 64, "Items did not move");

            helper.succeed();
        });
    }

    @GameTest(template = "25x3x25") //todo : fix whatever the heck is going on here
    public static void ManyInventoryLag(GameTestHelper helper) {
        // fill the platform with cables and barrels
        var sourceBlocks = new ArrayList<BlockPos>();
        var destBlocks   = new ArrayList<BlockPos>();
        for (int x = 0; x < 25; x++) {
//            for (int z = 0; z < 25; z++) {
            for (int z = 0; z < 24; z++) {
                helper.setBlock(new BlockPos(x, 2, z), SFMBlocks.CABLE_BLOCK.get());
                helper.setBlock(new BlockPos(x, 3, z), Blocks.BARREL);
                if (z % 2 == 0) {
                    sourceBlocks.add(new BlockPos(x, 3, z));
                    // fill the source chests with ingots
                    BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(new BlockPos(x, 3, z));
                    for (int i = 0; i < barrel.getContainerSize(); i++) {
                        barrel.setItem(i, new ItemStack(Items.IRON_INGOT, 64));
                    }
                } else {
                    destBlocks.add(new BlockPos(x, 3, z));
                }
            }
        }

        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(0, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(0, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "many inventory lag test"
                                    
                    EVERY 20 TICKS DO
                        INPUT FROM a
                        OUTPUT TO b
                    END
                """;

        // set the labels
        sourceBlocks.forEach(pos -> SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(pos)));
        destBlocks.forEach(pos -> SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(pos)));

        // load the program
        manager.setProgram(program);
        assertTrue(
                manager.getState() == ManagerBlockEntity.State.RUNNING,
                "Program did not start running " + DiskItem.getErrors(manager.getDisk().get())
        );
        List<Trigger> triggers  = manager.getProgram().triggers();
        var           existing  = triggers.get(0);
        var           startTime = new AtomicLong();
        var           endTime   = new AtomicLong();
        triggers.add(0, new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext manager) {
                return existing.shouldTick(manager);
            }

            @Override
            public void tick(ProgramContext context) {
                startTime.set(System.currentTimeMillis());
            }
        });
        triggers.add(new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext manager) {
                return existing.shouldTick(manager);
            }

            @Override
            public void tick(ProgramContext context) {
                endTime.set(System.currentTimeMillis());
            }
        });

        runAfterManagerFirstTick(helper, manager, () -> {
//            helper
//                    .getLevel()
//                    .getPlayers(p -> true)
//                    .forEach(player -> player.sendSystemMessage(Component
//                                                                        .literal(
//                                                                                "manyinventorylag manager execution took "
//                                                                                + (
//                                                                                        endTime.get() - startTime.get()
//                                                                                )
//                                                                                + "ms")
//                                                                        .withStyle(ChatFormatting.GREEN)));

            // ensure all the source chests are empty
            sourceBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    assertTrue(barrel.getItem(i).isEmpty(), "Items did not leave");
                }
            });
            // ensure all the dest chests are full
            destBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    assertTrue(barrel.getItem(i).getCount() == 64, "Items did not arrive");
                }
            });


            // remove all the items to speed up retests
            destBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    barrel.setItem(i, ItemStack.EMPTY);
                }
            });

            // ensure the program did not take too long
            assertTrue(
                    endTime.get() - startTime.get() < 1000,
                    "Program took too long to run: took " + (endTime.get() - startTime.get()) + "ms"
            );


            helper.succeed();
        });
    }

    @GameTest(template = "25x3x25") //todo : fix whatever the heck is going on here
    public static void FullSourceAndDest(GameTestHelper helper) {
        // fill the platform with cables and barrels
        var sourceBlocks = new ArrayList<BlockPos>();
        var destBlocks   = new ArrayList<BlockPos>();
        for (int x = 0; x < 25; x++) {
//            for (int z = 0; z < 25; z++) {
            for (int z = 0; z < 24; z++) {
                helper.setBlock(new BlockPos(x, 2, z), SFMBlocks.CABLE_BLOCK.get());
                helper.setBlock(new BlockPos(x, 3, z), Blocks.BARREL);
                if (z % 2 == 0) {
                    sourceBlocks.add(new BlockPos(x, 3, z));
                } else {
                    destBlocks.add(new BlockPos(x, 3, z));
                }

                // fill the source chests with ingots
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(new BlockPos(x, 3, z));
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    barrel.setItem(i, new ItemStack(Items.IRON_INGOT, 64));
                }
            }
        }

        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(0, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(0, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "many inventory lag test"
                                    
                    EVERY 20 TICKS DO
                        INPUT FROM a
                        OUTPUT TO b
                    END
                """;

        // set the labels
        sourceBlocks.forEach(pos -> SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(pos)));
        destBlocks.forEach(pos -> SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(pos)));

        // load the program
        manager.setProgram(program);
        assertTrue(
                manager.getState() == ManagerBlockEntity.State.RUNNING,
                "Program did not start running " + DiskItem.getErrors(manager.getDisk().get())
        );
        List<Trigger> triggers  = manager.getProgram().triggers();
        var           existing  = triggers.get(0);
        var           startTime = new AtomicLong();
        var           endTime   = new AtomicLong();
        triggers.add(0, new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext manager) {
                return existing.shouldTick(manager);
            }

            @Override
            public void tick(ProgramContext context) {
                startTime.set(System.currentTimeMillis());
            }
        });
        triggers.add(new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext manager) {
                return existing.shouldTick(manager);
            }

            @Override
            public void tick(ProgramContext context) {
                endTime.set(System.currentTimeMillis());
            }
        });

        runAfterManagerFirstTick(helper, manager, () -> {
            // ensure all the source chests are full
            sourceBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    assertTrue(barrel.getItem(i).getCount() == 64, "Items did not stay");
                }
            });
            // ensure all the dest chests are full
            destBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    assertTrue(barrel.getItem(i).getCount() == 64, "Items did not arrive");
                }
            });


            // remove all the items to speed up retests
//            sourceBlocks.forEach(pos -> {
//                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
//                for (int i = 0; i < barrel.getContainerSize(); i++) {
//                    barrel.setItem(i, ItemStack.EMPTY);
//                }
//            });
//            destBlocks.forEach(pos -> {
//                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
//                for (int i = 0; i < barrel.getContainerSize(); i++) {
//                    barrel.setItem(i, ItemStack.EMPTY);
//                }
//            });

            // ensure the program did not take too long
            assertTrue(
                    endTime.get() - startTime.get() < 1000,
                    "Program took too long to run: took " + (endTime.get() - startTime.get()) + "ms"
            );


            helper.succeed();
        });
    }


    @GameTest(template = "25x3x25")
    public static void GatherGoodSupplies(GameTestHelper helper) {
        helper.succeed();
    }

    public static void runAfterManagerFirstTick(GameTestHelper helper, ManagerBlockEntity manager, Runnable runnable) {
        var hasExecuted = new AtomicBoolean(false);
        manager.getProgram().triggers().add(new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext manager) {
                return manager.getManager().getTick() % 20 == 0;
            }

            @Override
            public void tick(ProgramContext context) {
                hasExecuted.set(true);
            }
        });
        IntStream.range(0, 200).forEach(i -> helper.runAfterDelay(i, () -> {
            if (hasExecuted.get()) {
                runnable.run();
            }
        }));
    }

    @GameTest(template = "25x3x25") // start with empty platform
    public static void CableNetworkFormation(GameTestHelper helper) {
        // create a row of cables
        for (int i = 0; i < 10; i++) {
            helper.setBlock(new BlockPos(i, 2, 0), SFMBlocks.CABLE_BLOCK.get());
        }

        var net = CableNetworkManager
                .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(0, 2, 0)))
                .get();
        // those cables should all be on the same network
        for (int i = 0; i < 10; i++) {
            assertTrue(CableNetworkManager
                               .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(i, 2, 0)))
                               .get() == net, "Networks did not merge");
        }

        // the network should only contain those cables
        assertTrue(net.getCables().size() == 10, "Network size did not match");

        // break a block in the middle of the cable
        helper.setBlock(new BlockPos(5, 2, 0), Blocks.AIR);
        // the network should split
        net = CableNetworkManager
                .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(0, 2, 0)))
                .get();
        // now we have a network of 5 cables and a network of 4 cables
        for (int i = 0; i < 5; i++) {
            assertTrue(CableNetworkManager
                               .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(i, 2, 0)))
                               .get() == net, "Networks did not merge");
        }
        net = CableNetworkManager
                .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(6, 2, 0)))
                .get();
        for (int i = 6; i < 10; i++) {
            assertTrue(CableNetworkManager
                               .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(i, 2, 0)))
                               .get() == net, "Networks did not merge");
        }

        // repair the cable
        helper.setBlock(new BlockPos(5, 2, 0), SFMBlocks.CABLE_BLOCK.get());
        // the network should merge
        net = CableNetworkManager
                .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(0, 2, 0)))
                .get();
        for (int i = 0; i < 10; i++) {
            assertTrue(CableNetworkManager
                               .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(i, 2, 0)))
                               .get() == net, "Networks did not merge");
        }

        // add cables in the corner
        helper.setBlock(new BlockPos(0, 2, 1), SFMBlocks.CABLE_BLOCK.get());
        helper.setBlock(new BlockPos(1, 2, 1), SFMBlocks.CABLE_BLOCK.get());
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(0, 2, 0)))
                           .get()
                           .getCables()
                           .size() == 12, "Network size did not match");

        // punch out the corner, the network should shrink by 1
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.AIR);
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(0, 2, 0)))
                           .get()
                           .getCables()
                           .size() == 11, "Network size did not match");


        // create a new network in a plus shape
        helper.setBlock(new BlockPos(15, 2, 15), SFMBlocks.CABLE_BLOCK.get());
        for (Direction value : Direction.values()) {
            helper.setBlock(new BlockPos(15, 2, 15).relative(value), SFMBlocks.CABLE_BLOCK.get());
        }
        // should all be on the same network
        net = CableNetworkManager
                .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(15, 2, 15)))
                .get();
        for (Direction value : Direction.values()) {
            assertTrue(
                    CableNetworkManager
                            .getOrRegisterNetwork(
                                    helper.getLevel(),
                                    helper.absolutePos(new BlockPos(15, 2, 15).relative(value))
                            )
                            .get() == net,
                    "Networks did not merge"
            );
        }

        // break the block in the middle
        helper.setBlock(new BlockPos(15, 2, 15), Blocks.AIR);
        // the network should split
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(15, 2, 15)))
                           .isEmpty(), "Network should not be present where the cable was removed from");
        var networks = new ArrayList<CableNetwork>();
        for (Direction value : Direction.values()) {
            networks.add(CableNetworkManager
                                 .getOrRegisterNetwork(
                                         helper.getLevel(),
                                         helper.absolutePos(new BlockPos(15, 2, 15).relative(value))
                                 )
                                 .get());
        }
        // make sure all the networks are different
        for (CableNetwork network : networks) {
            assertTrue(networks.stream().filter(n -> n == network).count() == 1, "Networks did not split");
        }

        // add the block back
        helper.setBlock(new BlockPos(15, 2, 15), SFMBlocks.CABLE_BLOCK.get());
        // the network should merge
        net = CableNetworkManager
                .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(15, 2, 15)))
                .get();
        for (Direction value : Direction.values()) {
            assertTrue(
                    CableNetworkManager
                            .getOrRegisterNetwork(
                                    helper.getLevel(),
                                    helper.absolutePos(new BlockPos(15, 2, 15).relative(value))
                            )
                            .get() == net,
                    "Networks did not merge"
            );
        }

        // lets also test having cables in more than just a straight line
        // we want corners with multiple cables adjacent

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                helper.setBlock(new BlockPos(7 + i, 2, 7 + j), SFMBlocks.CABLE_BLOCK.get());
            }
        }
        // make sure it's all in a single network
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(7, 2, 7)))
                           .get()
                           .getCables()
                           .size() == 25, "Network size did not match");
        // cut a line through it
        for (int i = 0; i < 5; i++) {
            helper.setBlock(new BlockPos(7 + i, 2, 9), Blocks.AIR);
        }
        // make sure the network disappeared where it was cut
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(7, 2, 9)))
                           .isEmpty(), "Network should not be present where the cable was removed from");
        // make sure new network of 10 is formed
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(7, 2, 8)))
                           .get()
                           .getCables()
                           .size() == 10, "Network size did not match");
        // make sure new network of 10 is formed
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(7, 2, 11)))
                           .get()
                           .getCables()
                           .size() == 10, "Network size did not match");
        // make sure the new networks are distinct
        assertTrue(CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(7, 2, 8)))
                           .get() != CableNetworkManager
                           .getOrRegisterNetwork(helper.getLevel(), helper.absolutePos(new BlockPos(7, 2, 11)))
                           .get(), "Networks did not split");


        helper.succeed();
    }


    @GameTest(template = "3x2x1") // start with empty platform
    public static void CauldronLavaMovement(GameTestHelper helper) {
        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos left = new BlockPos(2, 2, 0);
        helper.setBlock(left, Blocks.LAVA_CAULDRON);
        BlockPos right = new BlockPos(0, 2, 0);
        helper.setBlock(right, Blocks.CAULDRON);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "cauldron water test"
                                    
                    EVERY 20 TICKS DO
                        INPUT fluid:minecraft:lava FROM a
                        OUTPUT fluid:*:* TO b
                    END
                """;

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(left));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(right));

        // load the program
        manager.setProgram(program);

        assertManagerRunning(manager);
        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            helper.assertBlock(left, b -> b == Blocks.CAULDRON, "cauldron didn't empty");
            helper.assertBlockState(right, s -> s.getBlock() == Blocks.LAVA_CAULDRON, () -> "cauldron didn't fill");
            helper.succeed();
        });
    }

    public static void assertManagerRunning(ManagerBlockEntity manager) {
        assertTrue(
                manager.getState() == ManagerBlockEntity.State.RUNNING,
                "Program did not start running " + DiskItem.getErrors(manager.getDisk().get())
        );
    }

    @GameTest(template = "twochest")
    public static void MultiSlotInputOutput(GameTestHelper helper) {
        assertTwoChestTest(helper, """
                    EVERY 20 TICKS DO
                        INPUT FROM a TOP SIDE SLOTS 0,1,3-4,5
                        OUTPUT TO a SLOTS 2
                    END
                """, (left) -> {
            left.insertItem(0, new ItemStack(Items.DIAMOND, 5), false);
            left.insertItem(1, new ItemStack(Items.DIAMOND, 5), false);
            left.insertItem(3, new ItemStack(Items.DIAMOND, 5), false);
            left.insertItem(4, new ItemStack(Items.DIAMOND, 5), false);
            left.insertItem(5, new ItemStack(Items.DIAMOND, 5), false);
        }, right -> {
        }, left -> {
            assertTrue(left.getStackInSlot(0).isEmpty(), "slot 0 did not leave");
            assertTrue(left.getStackInSlot(1).isEmpty(), "slot 1 did not leave");
            assertTrue(left.getStackInSlot(3).isEmpty(), "slot 3 did not leave");
            assertTrue(left.getStackInSlot(4).isEmpty(), "slot 4 did not leave");
            assertTrue(left.getStackInSlot(5).isEmpty(), "slot 5 did not leave");
            assertTrue(left.getStackInSlot(2).getCount() == 25, "Items did not transfer to slot 2");
        }, right -> {
            assertTrue(
                    IntStream.range(0, right.getSlots()).allMatch(slot -> right.getStackInSlot(slot).isEmpty()),
                    "Chest b is not empty"
            );
        });
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
        var left = (helper.getBlockEntity(new BlockPos(2, 2, 0)))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();

        setupLeft.accept(left);
        setupRight.accept(right);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setProgram(program);
        assertManagerRunning(manager);

        helper.runAtTickTime(20 - helper.getTick() % 20, () -> {
            leftCheck.accept(left);
            rightCheck.accept(right);
            helper.succeed();
        });
    }
}
