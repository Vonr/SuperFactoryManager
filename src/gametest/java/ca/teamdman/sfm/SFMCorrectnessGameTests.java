package ca.teamdman.sfm;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.blockentity.PrintingPressBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

// https://github.dev/CompactMods/CompactMachines
// https://github.com/SocketMods/BaseDefense/blob/3b3cb4af26f4553c3438417cbb95f0d3fb707751/build.gradle#L74
// https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/build.gradle#L54
// https://github.com/mekanism/Mekanism/blob/1.16.x/build.gradle
// https://github.com/TwistedGate/ImmersivePetroleum/blob/1.16.5/build.gradle#L107
// https://github.com/MinecraftForge/MinecraftForge/blob/d7b137d1446377bfd1958f8a0e24f63819b81bfc/src/test/java/net/minecraftforge/debug/misc/GameTestTest.java#L155
// https://docs.minecraftforge.net/en/1.19.x/misc/gametest/
// https://github.com/MinecraftForge/MinecraftForge/blob/1.19.x/src/test/java/net/minecraftforge/debug/misc/GameTestTest.java#LL101-L116C6
// https://github.com/XFactHD/FramedBlocks/blob/1.19.4/src/main/java/xfacthd/framedblocks/api/test/TestUtils.java#L65-L87
@SuppressWarnings({"DataFlowIssue", "deprecation", "OptionalGetWithoutIsPresent", "DuplicatedCode"})
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMCorrectnessGameTests extends SFMGameTestBase {

    /**
     * Ensure that the manager state gets updated as the disk is inserted and the program is set
     */
    @GameTest(template = "1x2x1")
    public static void manager_state_update(GameTestHelper helper) {
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
                                   """.stripIndent());
        assertManagerRunning(manager);
        helper.succeed();
    }

    /**
     * Ensure moving everything a single stack of
     */
    @GameTest(template = "3x2x1")
    public static void move_1_stack(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos rightPos = new BlockPos(0, 2, 0);
        helper.setBlock(rightPos, Blocks.CHEST);
        BlockPos leftPos = new BlockPos(2, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);

        var rightChest = (helper.getBlockEntity(rightPos))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();
        var leftChest = helper.getBlockEntity(leftPos).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();

        leftChest.insertItem(0, new ItemStack(Blocks.DIRT, 64), false);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                       EVERY 20 TICKS DO
                                           INPUT FROM a
                                           OUTPUT TO b
                                       END
                                   """.stripIndent());

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftChest.getStackInSlot(0).isEmpty(), "Dirt did not move");
            assertTrue(rightChest.getStackInSlot(0).getCount() == 64, "Dirt did not move");
            helper.succeed();
        });
    }

    @GameTest(template = "3x2x1")
    public static void move_full_chest(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos rightPos = new BlockPos(0, 2, 0);
        helper.setBlock(rightPos, Blocks.CHEST);
        BlockPos leftPos = new BlockPos(2, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);


        var leftChest = (helper.getBlockEntity(leftPos)).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();

        var rightChest = (helper.getBlockEntity(rightPos))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();

        for (int i = 0; i < leftChest.getSlots(); i++) {
            leftChest.insertItem(i, new ItemStack(Blocks.DIRT, 64), false);
        }

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                       EVERY 20 TICKS DO
                                           INPUT FROM a
                                           OUTPUT TO b
                                       END
                                   """.stripIndent());
        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(
                    IntStream.range(0, leftChest.getSlots()).allMatch(slot -> leftChest.getStackInSlot(slot).isEmpty()),
                    "Dirt did not leave"
            );
            int count = rightChest.getSlots() * 64;
            int total = 0;
            for (int i = 0; i < rightChest.getSlots(); i++) {
                ItemStack x = rightChest.getStackInSlot(i);
                if (x.is(Items.DIRT)) {
                    total += rightChest.getStackInSlot(i).getCount();
                }
            }
            assertTrue(total == count, "Dirt did not arrive");
            helper.succeed();
        });
    }


    @GameTest(template = "3x4x3")
    public static void many_outputs(GameTestHelper helper) {
        BlockPos managerPos = new BlockPos(1, 2, 1);
        BlockPos sourcePos = new BlockPos(1, 3, 1);
        BlockPos dest1Pos = new BlockPos(2, 2, 1);
        BlockPos dest2Pos = new BlockPos(0, 2, 1);

        // set up inventories
        helper.setBlock(sourcePos, Blocks.CHEST);
        helper.setBlock(dest1Pos, Blocks.CHEST);
        helper.setBlock(dest2Pos, Blocks.CHEST);


        var sourceInv = (helper.getBlockEntity(sourcePos))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();

        var dest1Inv = (helper.getBlockEntity(dest1Pos)).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();

        var dest2Inv = (helper.getBlockEntity(dest2Pos)).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();

        for (int i = 0; i < sourceInv.getSlots(); i++) {
            sourceInv.insertItem(i, new ItemStack(Blocks.DIRT, 64), false);
        }

        // set up manager
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                       EVERY 20 TICKS DO
                                           INPUT FROM source
                                           OUTPUT 64 dirt TO EACH dest
                                       END
                                   """.stripIndent());
        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "source", helper.absolutePos(sourcePos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "dest", helper.absolutePos(dest1Pos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "dest", helper.absolutePos(dest2Pos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            int found = IntStream
                    .range(0, sourceInv.getSlots())
                    .mapToObj(sourceInv::getStackInSlot)
                    .mapToInt(ItemStack::getCount)
                    .sum();
            assertTrue(found == 64 * (sourceInv.getSlots() - 2), "Dirt did not leave (found " + found + " (" + (
                    found > 64 ? found / 64 + "x stacks + " + found % 64 : found
            ) + " dirt))");
            int total;
            total = 0;
            for (int i = 0; i < dest1Inv.getSlots(); i++) {
                ItemStack x = dest1Inv.getStackInSlot(i);
                if (x.is(Items.DIRT)) {
                    total += dest1Inv.getStackInSlot(i).getCount();
                }
            }
            assertTrue(total == 64, "Dirt did not arrive properly 1");
            total = 0;
            for (int i = 0; i < dest2Inv.getSlots(); i++) {
                ItemStack x = dest2Inv.getStackInSlot(i);
                if (x.is(Items.DIRT)) {
                    total += dest2Inv.getStackInSlot(i).getCount();
                }
            }
            assertTrue(total == 64, "Dirt did not arrive properly 2");
            helper.succeed();
        });
    }

    @GameTest(template = "3x2x1")
    public static void retain_5(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos leftPos = new BlockPos(2, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);
        BlockPos rightPos = new BlockPos(0, 2, 0);
        helper.setBlock(rightPos, Blocks.CHEST);

        var rightChest = (helper.getBlockEntity(rightPos))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();
        var leftChest = (helper.getBlockEntity(leftPos)).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();

        leftChest.insertItem(0, new ItemStack(Blocks.DIRT, 64), false);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                      INPUT RETAIN 5 FROM a
                                      OUTPUT TO b
                                   END
                                   """.stripIndent());
        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftChest.getStackInSlot(0).getCount() == 5, "Dirt did not move");
            assertTrue(rightChest.getStackInSlot(0).getCount() == 64 - 5, "Dirt did not move");
            helper.succeed();
        });
    }

    @GameTest(template = "3x2x1")
    public static void move_multiple_item_names(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos rightPos = new BlockPos(0, 2, 0);
        helper.setBlock(rightPos, Blocks.CHEST);
        BlockPos leftPos = new BlockPos(2, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);

        var leftChest = (helper.getBlockEntity(leftPos)).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();
        var rightChest = (helper.getBlockEntity(rightPos))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();

        leftChest.insertItem(0, new ItemStack(Items.IRON_INGOT, 64), false);
        leftChest.insertItem(1, new ItemStack(Items.STONE, 64), false);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
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
                                   """.stripIndent());
        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftChest.getStackInSlot(0).getCount() == 64 - 2, "Iron ingots did not retain");
            assertTrue(leftChest.getStackInSlot(1).getCount() == 64 - 10, "Stone did not retain");
            assertTrue(rightChest.getStackInSlot(0).getCount() == 2, "Iron ingots did not move");
            assertTrue(rightChest.getStackInSlot(1).getCount() == 10, "Stone did not move");
            helper.succeed();
        });
    }

    /**
     * Ensure that cauldrons can be treated as water fluid holders
     */
    @GameTest(template = "3x2x1")
    public static void move_cauldron_water(GameTestHelper helper) {
        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos left = new BlockPos(2, 2, 0);
        helper.setBlock(left, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
        BlockPos right = new BlockPos(0, 2, 0);
        helper.setBlock(right, Blocks.CAULDRON);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(left));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(right));

        // load the program
        manager.setProgram("""
                                       NAME "cauldron water test"
                                                       
                                       EVERY 20 TICKS DO
                                           INPUT fluid:minecraft:water FROM a
                                           OUTPUT fluid:*:* TO b
                                       END
                                   """.stripIndent());

        assertManagerRunning(manager);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
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

    /**
     * Ensure that a cauldrons can be treated as a lava fluid holder
     */
    @GameTest(template = "3x2x1")
    public static void move_cauldron_lava(GameTestHelper helper) {
        // fill in the blocks needed for the test
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos left = new BlockPos(2, 2, 0);
        helper.setBlock(left, Blocks.LAVA_CAULDRON.defaultBlockState());
        BlockPos right = new BlockPos(0, 2, 0);
        helper.setBlock(right, Blocks.CAULDRON);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(left));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(right));

        // load the program
        manager.setProgram("""
                                       NAME "cauldron lava test"
                                                       
                                       EVERY 20 TICKS DO
                                           INPUT fluid:minecraft:lava FROM a
                                           OUTPUT fluid:*:* TO b
                                       END
                                   """.stripIndent());

        assertManagerRunning(manager);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            helper.assertBlock(left, b -> b == Blocks.CAULDRON, "cauldron didn't empty");
            helper.assertBlockState(right, s -> s.getBlock() == Blocks.LAVA_CAULDRON, () -> "cauldron didn't fill");
            helper.succeed();
        });
    }

    @GameTest(template = "25x4x25")
    public static void cable_spiral(GameTestHelper helper) {
        BlockPos start = new BlockPos(0, 2, 0);
        BlockPos end = new BlockPos(12, 2, 12);

        var len = 24;
        var dir = Direction.EAST;
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

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(start));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(end));

        // load the program
        manager.setProgram("""
                                       NAME "long cable test"
                                                       
                                       EVERY 20 TICKS DO
                                           INPUT FROM a
                                           OUTPUT TO b
                                       END
                                   """.stripIndent());

        assertManagerRunning(manager);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            // ensure item arrived
            assertTrue(endChest.getItem(0).getCount() == 64, "Items did not move");
            // ensure item left
            assertTrue(startChest.getItem(0).isEmpty(), "Items did not leave");
            helper.succeed();
        });
    }


    @GameTest(template = "3x4x3")
    public static void regression_crash_type_mixing(GameTestHelper helper) {
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

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(left));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(front));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(right));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(back));

        // load the program
        manager.setProgram("""
                                       NAME "water crash test"
                                                       
                                       every 20 ticks do
                                           INPUT  item:minecraft:stick, fluid:minecraft:water FROM a
                                           OUTPUT stick, fluid:minecraft:water TO b
                                       end
                                   """.stripIndent());

        assertManagerRunning(manager);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
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

    @GameTest(template = "25x4x25") // start with empty platform
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
            assertTrue(CableNetworkManager
                               .getOrRegisterNetwork(
                                       helper.getLevel(),
                                       helper.absolutePos(new BlockPos(15, 2, 15).relative(value))
                               )
                               .get()
                       == net, "Networks did not merge");
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
            assertTrue(CableNetworkManager
                               .getOrRegisterNetwork(
                                       helper.getLevel(),
                                       helper.absolutePos(new BlockPos(15, 2, 15).relative(value))
                               )
                               .get()
                       == net, "Networks did not merge");
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
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            helper.assertBlock(left, b -> b == Blocks.CAULDRON, "cauldron didn't empty");
            helper.assertBlockState(right, s -> s.getBlock() == Blocks.LAVA_CAULDRON, () -> "cauldron didn't fill");
            helper.succeed();
        });
    }

    @GameTest(template = "3x2x1")
    public static void move_slots(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        BlockPos rightPos = new BlockPos(0, 2, 0);
        helper.setBlock(rightPos, Blocks.CHEST);
        BlockPos leftPos = new BlockPos(2, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);

        var rightChest = (helper.getBlockEntity(rightPos))
                .getCapability(ForgeCapabilities.ITEM_HANDLER)
                .resolve()
                .get();
        var leftChest = (helper.getBlockEntity(leftPos)).getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();

        leftChest.insertItem(0, new ItemStack(Items.DIAMOND, 5), false);
        leftChest.insertItem(1, new ItemStack(Items.DIAMOND, 5), false);
        leftChest.insertItem(3, new ItemStack(Items.DIAMOND, 5), false);
        leftChest.insertItem(4, new ItemStack(Items.DIAMOND, 5), false);
        leftChest.insertItem(5, new ItemStack(Items.DIAMOND, 5), false);

        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(1, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                       EVERY 20 TICKS DO
                                           INPUT FROM a TOP SIDE SLOTS 0,1,3-4,5
                                           OUTPUT TO a SLOTS 2
                                       END
                                   """.stripIndent());

        // set the labels
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftChest.getStackInSlot(0).isEmpty(), "slot 0 did not leave");
            assertTrue(leftChest.getStackInSlot(1).isEmpty(), "slot 1 did not leave");
            assertTrue(leftChest.getStackInSlot(3).isEmpty(), "slot 3 did not leave");
            assertTrue(leftChest.getStackInSlot(4).isEmpty(), "slot 4 did not leave");
            assertTrue(leftChest.getStackInSlot(5).isEmpty(), "slot 5 did not leave");
            assertTrue(leftChest.getStackInSlot(2).getCount() == 25, "Items did not transfer to slot 2");
            assertTrue(IntStream
                               .range(0, rightChest.getSlots())
                               .allMatch(slot -> rightChest.getStackInSlot(slot).isEmpty()), "Chest b is not empty");
            helper.succeed();
        });
    }


    @GameTest(template = "3x4x3")
    public static void printing_press_clone_program(GameTestHelper helper) {
        var printingPos = new BlockPos(1, 2, 1);
        var pistonPos = new BlockPos(1, 4, 1);
        var woodPos = new BlockPos(0, 4, 1);
        var buttonPos = new BlockPos(0, 4, 0);
        var chestPos = new BlockPos(0, 2, 1);

        helper.setBlock(printingPos, SFMBlocks.PRINTING_PRESS_BLOCK.get());
        helper.setBlock(pistonPos, Blocks.PISTON.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.DOWN));
        helper.setBlock(woodPos, Blocks.OAK_PLANKS);
        helper.setBlock(buttonPos, Blocks.STONE_BUTTON);
        helper.setBlock(chestPos, Blocks.CHEST);

        var printingPress = (PrintingPressBlockEntity) helper.getBlockEntity(printingPos);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BLACK_DYE));
        BlockState pressState = helper.getBlockState(printingPos);
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(SFMItems.DISK_ITEM.get()));
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );
        var disk = new ItemStack(SFMItems.DISK_ITEM.get());
        DiskItem.setProgram(disk, """
                    EVERY 20 TICKS DO
                        INPUT FROM a TOP SIDE SLOTS 0,1,3-4,5
                        OUTPUT TO a SLOTS 2
                    END
                """.stripIndent());
        player.setItemInHand(InteractionHand.MAIN_HAND, FormItem.getForm(disk));
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );

        BlockState buttonState = helper.getBlockState(buttonPos);
        buttonState.getBlock().use(
                buttonState,
                helper.getLevel(),
                helper.absolutePos(buttonPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );

        helper.runAfterDelay(5, () -> {
            pressState.getBlock().use(
                    pressState,
                    helper.getLevel(),
                    helper.absolutePos(printingPos),
                    player,
                    InteractionHand.MAIN_HAND,
                    new BlockHitResult(
                            new Vec3(0.5, 0.5, 0.5),
                            Direction.UP,
                            helper.absolutePos(printingPos),
                            false
                    )
            );
            ItemStack held = player.getMainHandItem();
            if (held.is(SFMItems.DISK_ITEM.get()) && DiskItem.getProgram(held).equals(DiskItem.getProgram(disk))) {
                helper
                        .getBlockEntity(chestPos)
                        .getCapability(ForgeCapabilities.ITEM_HANDLER)
                        .ifPresent(c -> c.insertItem(0, held, false));
                assertTrue(printingPress.getInk().isEmpty(), "Ink was not consumed");
                assertTrue(printingPress.getPaper().isEmpty(), "Paper was not consumed");
                assertTrue(!printingPress.getForm().isEmpty(), "Form should not be consumed");
                helper.succeed();
            } else {
                helper.fail("Disk was not cloned");
            }
        });
    }

    @GameTest(template = "1x2x1")
    public static void printing_press_insertion_extraction(GameTestHelper helper) {
        var pos = new BlockPos(0, 2, 0);
        helper.setBlock(pos, SFMBlocks.PRINTING_PRESS_BLOCK.get());
        var printingPress = (PrintingPressBlockEntity) helper.getBlockEntity(pos);
        var player = helper.makeMockPlayer();
        // put black dye in player hand
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BLACK_DYE, 23));
        // right click on printing press
        BlockState pressState = helper.getBlockState(pos);
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert the ink was inserted
        assertTrue(!printingPress.getInk().isEmpty(), "Ink was not inserted");
        assertTrue(player.getMainHandItem().isEmpty(), "Ink was not taken from hand");
        // put book in player hand
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOOK));
        // right click on printing press
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert the book was inserted
        assertTrue(!printingPress.getPaper().isEmpty(), "Paper was not inserted");
        assertTrue(player.getMainHandItem().isEmpty(), "Paper was not taken from hand");
        // put form in player hand
        var form = FormItem.getForm(new ItemStack(Items.WRITTEN_BOOK));
        player.setItemInHand(InteractionHand.MAIN_HAND, form.copy());
        // right click on printing press
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert the form was inserted
        assertTrue(!printingPress.getForm().isEmpty(), "Form was not inserted");
        assertTrue(player.getMainHandItem().isEmpty(), "Form was not taken from hand");

        // pull out item
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        // right click on printing press
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert the paper was extracted
        assertTrue(printingPress.getPaper().isEmpty(), "Paper was not extracted");
        assertTrue(!player.getMainHandItem().isEmpty(), "Paper was not given to player");
        assertTrue(player.getMainHandItem().is(Items.BOOK), "Paper doesn't match");
        assertTrue(player.getMainHandItem().getCount() == 1, "Paper wrong count");

        // pull out an item
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        // right click on printing press
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert the form was extracted
        assertTrue(printingPress.getForm().isEmpty(), "Form was not extracted");
        assertTrue(!player.getMainHandItem().isEmpty(), "Form was not given to player");
        assertTrue(ItemStack.isSameItemSameTags(player.getMainHandItem(), form), "Form doesn't match");
        // pull out item
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        // right click on printing press
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert the ink was extracted
        assertTrue(printingPress.getInk().isEmpty(), "Ink was not extracted");
        assertTrue(!player.getMainHandItem().isEmpty(), "Ink was not given to player");
        assertTrue(player.getMainHandItem().is(Items.BLACK_DYE), "Ink doesn't match");
        assertTrue(player.getMainHandItem().getCount() == 23, "Ink wrong count");
        // try to pull out another item
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        // right click on printing press
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(pos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(pos),
                        false
                )
        );
        // assert nothing was extracted
        assertTrue(player.getMainHandItem().isEmpty(), "Nothing should have been extracted");
        helper.succeed();
    }

    @GameTest(template = "3x4x3")
    public static void printing_press_clone_enchantment(GameTestHelper helper) {
        var printingPos = new BlockPos(1, 2, 1);
        var pistonPos = new BlockPos(1, 4, 1);
        var woodPos = new BlockPos(0, 4, 1);
        var buttonPos = new BlockPos(0, 4, 0);
        var chestPos = new BlockPos(0, 2, 1);

        helper.setBlock(printingPos, SFMBlocks.PRINTING_PRESS_BLOCK.get());
        helper.setBlock(pistonPos, Blocks.PISTON.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.DOWN));
        helper.setBlock(woodPos, Blocks.OAK_PLANKS);
        helper.setBlock(buttonPos, Blocks.STONE_BUTTON);
        helper.setBlock(chestPos, Blocks.CHEST);

        var printingPress = (PrintingPressBlockEntity) helper.getBlockEntity(printingPos);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(SFMItems.EXPERIENCE_GOOP_ITEM.get(), 10));
        BlockState pressState = helper.getBlockState(printingPos);
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOOK));
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );
        ItemStack reference = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                Enchantments.SHARPNESS,
                3
        ));
        player.setItemInHand(InteractionHand.MAIN_HAND, FormItem.getForm(reference));
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );

        BlockState buttonState = helper.getBlockState(buttonPos);
        buttonState.getBlock().use(
                buttonState,
                helper.getLevel(),
                helper.absolutePos(buttonPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );

        helper.runAfterDelay(5, () -> {
            pressState.getBlock().use(
                    pressState,
                    helper.getLevel(),
                    helper.absolutePos(printingPos),
                    player,
                    InteractionHand.MAIN_HAND,
                    new BlockHitResult(
                            new Vec3(0.5, 0.5, 0.5),
                            Direction.UP,
                            helper.absolutePos(printingPos),
                            false
                    )
            );
            ItemStack held = player.getMainHandItem();
            if (ItemStack.isSameItemSameTags(held, reference)) {
                helper
                        .getBlockEntity(chestPos)
                        .getCapability(ForgeCapabilities.ITEM_HANDLER)
                        .ifPresent(c -> c.insertItem(0, held, false));
                assertTrue(printingPress.getInk().getCount() == 9, "Ink was not consumed properly");
                assertTrue(printingPress.getPaper().isEmpty(), "Paper was not consumed");
                assertTrue(!printingPress.getForm().isEmpty(), "Form should not be consumed");
                helper.succeed();
            } else {
                helper.fail("cloned item wasnt same");
            }
        });
    }

    @GameTest(template = "3x4x3")
    public static void falling_anvil_program_form(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.IRON_BLOCK);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));
        ItemStack disk = new ItemStack(SFMItems.DISK_ITEM.get());
        DiskItem.setProgram(disk, """
                    NAME "falling anvil test"
                    EVERY 20 TICKS DO
                        INPUT FROM a TOP SIDE SLOTS 0,1,3-4,5
                        OUTPUT TO a SLOTS 2
                    END
                """.stripIndent());
        helper
                .getLevel()
                .addFreshEntity(new ItemEntity(
                        helper.getLevel(),
                        pos.x, pos.y, pos.z,
                        disk,
                        0, 0, 0
                ));
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);
        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            if (found.stream().anyMatch(e -> ItemStack.isSameItemSameTags(e.getItem(), FormItem.getForm(disk)))) {
                helper.succeed();
            } else {
                helper.fail("no form found");
            }
        });
    }

    @GameTest(template = "3x4x3")
    public static void falling_anvil_enchantment_form(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.IRON_BLOCK);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));
        ItemStack reference = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                Enchantments.SHARPNESS,
                3
        ));
        helper
                .getLevel()
                .addFreshEntity(new ItemEntity(
                        helper.getLevel(),
                        pos.x, pos.y, pos.z,
                        reference,
                        0, 0, 0
                ));
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);
        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            if (found.stream().anyMatch(e -> ItemStack.isSameItemSameTags(e.getItem(), FormItem.getForm(reference)))) {
                helper.succeed();
            } else {
                helper.fail("no form found");
            }
        });
    }

    @GameTest(template = "3x4x3")
    public static void falling_anvil_disenchant(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.OBSIDIAN);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));
        helper
                .getLevel()
                .addFreshEntity(new ItemEntity(
                        helper.getLevel(),
                        pos.x, pos.y, pos.z,
                        new ItemStack(Items.BOOK, 16),
                        0, 0, 0
                ));
        var axe = new ItemStack(Items.GOLDEN_AXE);
        axe.enchant(Enchantments.BLOCK_EFFICIENCY, 3);
        axe.enchant(Enchantments.SHARPNESS, 2);
        helper.getLevel().addFreshEntity(new ItemEntity(
                helper.getLevel(),
                pos.x, pos.y, pos.z,
                axe,
                0, 0, 0
        ));
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);
        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            boolean foundDisenchantedAxe = found
                    .stream()
                    .anyMatch(e -> ItemStack.isSameItemSameTags(e.getItem(), new ItemStack(Items.GOLDEN_AXE)));
            boolean foundEfficiencyBook = found
                    .stream()
                    .anyMatch(e -> ItemStack.isSameItemSameTags(
                            e.getItem(),
                            EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                                    Enchantments.BLOCK_EFFICIENCY,
                                    3
                            ))
                    ));
            boolean foundSharpnessBook = found
                    .stream()
                    .anyMatch(e -> ItemStack.isSameItemSameTags(
                            e.getItem(),
                            EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Enchantments.SHARPNESS, 2))
                    ));
            boolean foundRemainingBooks = found
                                                  .stream()
                                                  .filter(e -> e.getItem().is(Items.BOOK))
                                                  .mapToInt(e -> e.getItem().getCount())
                                                  .sum() == 16 - 2;
            if (foundDisenchantedAxe && foundEfficiencyBook && foundSharpnessBook && foundRemainingBooks) {
                helper.succeed();
            } else {
                helper.fail("disenchant failed");
            }
        });
    }

    @GameTest(template = "3x4x3")
    public static void falling_anvil_xp_shard(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.OBSIDIAN);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));
        helper
                .getLevel()
                .addFreshEntity(new ItemEntity(
                        helper.getLevel(),
                        pos.x, pos.y, pos.z,
                        EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                                Enchantments.SHARPNESS,
                                3
                        )),
                        0, 0, 0
                ));
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);
        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            assertTrue(found.size() == 1, "should only be one item");
            assertTrue(found.get(0).getItem().is(SFMItems.EXPERIENCE_SHARD_ITEM.get()), "should be an xp shard");
            assertTrue(found.get(0).getItem().getCount() == 1, "should only be one");
            helper.succeed();
        });
    }

    @GameTest(template = "3x4x3")
    public static void falling_anvil_xp_shard_many(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.OBSIDIAN);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));
        ItemStack enchBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                Enchantments.SHARPNESS,
                3
        ));
        for (int i = 0; i < 10; i++) {
            helper
                    .getLevel()
                    .addFreshEntity(new ItemEntity(
                            helper.getLevel(),
                            pos.x, pos.y, pos.z,
                            enchBook,
                            0, 0, 0
                    ));
        }
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);
        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            assertTrue(
                    found.stream().allMatch(e -> e.getItem().is(SFMItems.EXPERIENCE_SHARD_ITEM.get())),
                    "should only be xp shards"
            );
            assertTrue(found.stream().mapToInt(e -> e.getItem().getCount()).sum() == 10, "bad count");
            helper.succeed();
        });
    }

    @GameTest(template = "1x2x1")
    public static void disk_item_clientside_regression(GameTestHelper helper) {
        var stack = new ItemStack(SFMItems.DISK_ITEM.get());
        stack.getDisplayName();
        stack.getHoverName();
        stack.getItem().getName(stack);
        stack.getItem().appendHoverText(stack, helper.getLevel(), new ArrayList<>(), TooltipFlag.Default.NORMAL);
        Vec3 pos = helper.absoluteVec(new Vec3(0.5, 1, 0.5));
        ItemEntity itemEntity = new ItemEntity(helper.getLevel(), pos.x, pos.y, pos.z, stack, 0, 0, 0);
        helper.getLevel().addFreshEntity(itemEntity);
        helper.succeed();
    }

    @GameTest(template = "1x2x1")
    public static void program_crlf_line_endings_conversion(GameTestHelper helper) {
        var managerPos = new BlockPos(0, 2, 0);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        String program = """
                NAME "line endings test"
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT TO b
                END
                """.stripIndent();
        String programWithWindowsLineEndings = program.replaceAll("\n", "\r\n");
        manager.setProgram(programWithWindowsLineEndings);
        if (manager.getProgramString().get().equals(program)) {
            helper.succeed();
        } else {
            helper.fail(String.format(
                    "program string was not converted correctly: %s",
                    manager.getProgramString().get()
            ));
        }
    }

    @GameTest(template = "3x2x1")
    public static void comparison_gt(GameTestHelper helper) {
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);
        helper.setBlock(rightPos, Blocks.CHEST);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var left = (ChestBlockEntity) helper.getBlockEntity(leftPos);
        var right = (ChestBlockEntity) helper.getBlockEntity(rightPos);
        var manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        left.setItem(0, new ItemStack(Items.DIAMOND, 64));
        left.setItem(1, new ItemStack(Items.DIAMOND, 64));
        left.setItem(2, new ItemStack(Items.IRON_INGOT, 12));
        right.setItem(0, new ItemStack(Items.STICK, 13));
        right.setItem(1, new ItemStack(Items.STICK, 64));
        right.setItem(2, new ItemStack(Items.DIRT, 1));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "comparison_gt test"
                                   EVERY 20 TICKS DO
                                       IF left HAS GT 100 diamond THEN
                                           -- should happen
                                           INPUT diamond FROM left
                                           OUTPUT diamond TO right
                                       END
                                       IF left HAS GT 300 iron_ingot THEN
                                           -- should not happen
                                           INPUT iron_ingot FROM left
                                           OUTPUT iron_ingot TO right
                                       END
                                       IF right HAS > 10 stick THEN
                                           -- should happen
                                           INPUT stick FROM right
                                           OUTPUT stick TO left
                                       END
                                       if right has > 0 dirt then
                                           -- should happen
                                           input dirt from right
                                           output dirt to left
                                       end
                                   END
                                   """);

        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "left", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "right", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            int leftDiamondCount = count(left, Items.DIAMOND);
            int leftIronCount = count(left, Items.IRON_INGOT);
            int leftStickCount = count(left, Items.STICK);
            int leftDirtCount = count(left, Items.DIRT);
            int rightDiamondCount = count(right, Items.DIAMOND);
            int rightIronCount = count(right, Items.IRON_INGOT);
            int rightStickCount = count(right, Items.STICK);
            int rightDirtCount = count(right, Items.DIRT);
            // the diamonds should have moved from left to right
            assertTrue(leftDiamondCount == 0, "left should have no diamonds");
            assertTrue(rightDiamondCount == 64 * 2, "right should have 100 diamonds");
            // the iron should have stayed in left
            assertTrue(leftIronCount == 12, "left should have 12 iron ingots");
            assertTrue(rightIronCount == 0, "right should have no iron ingots");
            // the sticks should have moved from right to left
            assertTrue(rightStickCount == 0, "right should have no sticks");
            assertTrue(leftStickCount == 77, "left should have 77 sticks");
            // the dirt should have moved from right to left
            assertTrue(rightDirtCount == 0, "right should have no dirt");
            assertTrue(leftDirtCount == 1, "left should have 1 dirt");
        });
    }


    @GameTest(template = "3x2x1")
    public static void comparison_ge(GameTestHelper helper) {
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);
        helper.setBlock(rightPos, Blocks.CHEST);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var left = (ChestBlockEntity) helper.getBlockEntity(leftPos);
        var right = (ChestBlockEntity) helper.getBlockEntity(rightPos);
        var manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        left.setItem(0, new ItemStack(Items.DIAMOND, 64));
        left.setItem(1, new ItemStack(Items.DIAMOND, 64));
        left.setItem(2, new ItemStack(Items.IRON_INGOT, 12));
        right.setItem(0, new ItemStack(Items.STICK, 13));
        right.setItem(1, new ItemStack(Items.STICK, 64));
        right.setItem(2, new ItemStack(Items.DIRT, 1));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "comparison_ge test"
                                   EVERY 20 TICKS DO
                                       IF left HAS GE 129 diamond THEN
                                           -- should not happen
                                           INPUT diamond FROM left
                                           OUTPUT diamond TO right
                                       END
                                       IF left HAS GE 12 iron_ingot THEN
                                           -- should happen
                                           INPUT iron_ingot FROM left
                                           OUTPUT iron_ingot TO right
                                       END
                                       IF right HAS >= 13 stick THEN
                                           -- should happen
                                           INPUT stick FROM right
                                           OUTPUT stick TO left
                                       END
                                       if right has >= 1 dirt then
                                           -- should happen
                                           input dirt from right
                                           output dirt to left
                                       end
                                   END
                                   """);

        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "left", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "right", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            int leftDiamondCount = count(left, Items.DIAMOND);
            int leftIronCount = count(left, Items.IRON_INGOT);
            int leftStickCount = count(left, Items.STICK);
            int leftDirtCount = count(left, Items.DIRT);
            int rightDiamondCount = count(right, Items.DIAMOND);
            int rightIronCount = count(right, Items.IRON_INGOT);
            int rightStickCount = count(right, Items.STICK);
            int rightDirtCount = count(right, Items.DIRT);
            // the diamonds should have moved from left to right
            assertTrue(leftDiamondCount == 64 * 2, "left should have 128 diamonds");
            assertTrue(rightDiamondCount == 0, "right should have no diamonds");
            // the iron should have moved from left to right
            assertTrue(leftIronCount == 0, "left should have no iron ingots");
            assertTrue(rightIronCount == 12, "right should have 12 iron ingots");
            // the sticks should have moved from right to left
            assertTrue(rightStickCount == 0, "right should have no sticks");
            assertTrue(leftStickCount == 77, "left should have 77 sticks");
            // the dirt should have moved from right to left
            assertTrue(rightDirtCount == 0, "right should have no dirt");
            assertTrue(leftDirtCount == 1, "left should have 1 dirt");
        });
    }


    @GameTest(template = "3x2x1")
    public static void comparison_eq(GameTestHelper helper) {
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);
        helper.setBlock(rightPos, Blocks.CHEST);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var left = (ChestBlockEntity) helper.getBlockEntity(leftPos);
        var right = (ChestBlockEntity) helper.getBlockEntity(rightPos);
        var manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        left.setItem(0, new ItemStack(Items.DIAMOND, 64));
        left.setItem(1, new ItemStack(Items.DIAMOND, 64));
        left.setItem(2, new ItemStack(Items.IRON_INGOT, 12));
        right.setItem(0, new ItemStack(Items.STICK, 13));
        right.setItem(1, new ItemStack(Items.STICK, 64));
        right.setItem(2, new ItemStack(Items.DIRT, 1));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "comparison_eq test"
                                   EVERY 20 TICKS DO
                                       IF left HAS eq 129 diamond THEN
                                           -- should not happen
                                           INPUT diamond FROM left
                                           OUTPUT diamond TO right
                                       END
                                       IF left HAS = 12 iron_ingot THEN
                                           -- should happen
                                           INPUT iron_ingot FROM left
                                           OUTPUT iron_ingot TO right
                                       END
                                       IF right HAS eq 77 stick THEN
                                           -- should happen
                                           INPUT stick FROM right
                                           OUTPUT stick TO left
                                       END
                                       if right has = 1 dirt then
                                           -- should happen
                                           input dirt from right
                                           output dirt to left
                                       end
                                   END
                                   """);

        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "left", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "right", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            int leftDiamondCount = count(left, Items.DIAMOND);
            int leftIronCount = count(left, Items.IRON_INGOT);
            int leftStickCount = count(left, Items.STICK);
            int leftDirtCount = count(left, Items.DIRT);
            int rightDiamondCount = count(right, Items.DIAMOND);
            int rightIronCount = count(right, Items.IRON_INGOT);
            int rightStickCount = count(right, Items.STICK);
            int rightDirtCount = count(right, Items.DIRT);
            // the diamonds should have moved from left to right
            assertTrue(leftDiamondCount == 64 * 2, "left should have 128 diamonds");
            assertTrue(rightDiamondCount == 0, "right should have no diamonds");
            // the iron should have moved from left to right
            assertTrue(leftIronCount == 0, "left should have no iron ingots");
            assertTrue(rightIronCount == 12, "right should have 12 iron ingots");
            // the sticks should have moved from right to left
            assertTrue(rightStickCount == 0, "right should have no sticks");
            assertTrue(leftStickCount == 77, "left should have 77 sticks");
            // the dirt should have moved from right to left
            assertTrue(rightDirtCount == 0, "right should have no dirt");
            assertTrue(leftDirtCount == 1, "left should have 1 dirt");
        });
    }


    @GameTest(template = "3x2x1")
    public static void comparison_lt(GameTestHelper helper) {
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);
        helper.setBlock(rightPos, Blocks.CHEST);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var left = (ChestBlockEntity) helper.getBlockEntity(leftPos);
        var right = (ChestBlockEntity) helper.getBlockEntity(rightPos);
        var manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        left.setItem(0, new ItemStack(Items.DIAMOND, 64));
        left.setItem(1, new ItemStack(Items.DIAMOND, 64));
        left.setItem(2, new ItemStack(Items.IRON_INGOT, 12));
        right.setItem(0, new ItemStack(Items.STICK, 13));
        right.setItem(1, new ItemStack(Items.STICK, 64));
        right.setItem(2, new ItemStack(Items.DIRT, 1));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "comparison_lt test"
                                   EVERY 20 TICKS DO
                                       IF left HAS lt 10 diamond THEN
                                           -- should not happen
                                           INPUT diamond FROM left
                                           OUTPUT diamond TO right
                                       END
                                       IF left HAS < 200 iron_ingot THEN
                                           -- should happen
                                           INPUT iron_ingot FROM left
                                           OUTPUT iron_ingot TO right
                                       END
                                       IF right HAS < 78 stick THEN
                                           -- should happen
                                           INPUT stick FROM right
                                           OUTPUT stick TO left
                                       END
                                       if right has < 3 dirt then
                                           -- should happen
                                           input dirt from right
                                           output dirt to left
                                       end
                                   END
                                   """);

        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "left", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "right", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            int leftDiamondCount = count(left, Items.DIAMOND);
            int leftIronCount = count(left, Items.IRON_INGOT);
            int leftStickCount = count(left, Items.STICK);
            int leftDirtCount = count(left, Items.DIRT);
            int rightDiamondCount = count(right, Items.DIAMOND);
            int rightIronCount = count(right, Items.IRON_INGOT);
            int rightStickCount = count(right, Items.STICK);
            int rightDirtCount = count(right, Items.DIRT);
            // the diamonds should have moved from left to right
            assertTrue(leftDiamondCount == 64 * 2, "left should have 128 diamonds");
            assertTrue(rightDiamondCount == 0, "right should have no diamonds");
            // the iron should have moved from left to right
            assertTrue(leftIronCount == 0, "left should have no iron ingots");
            assertTrue(rightIronCount == 12, "right should have 12 iron ingots");
            // the sticks should have moved from right to left
            assertTrue(rightStickCount == 0, "right should have no sticks");
            assertTrue(leftStickCount == 77, "left should have 77 sticks");
            // the dirt should have moved from right to left
            assertTrue(rightDirtCount == 0, "right should have no dirt");
            assertTrue(leftDirtCount == 1, "left should have 1 dirt");
        });
    }


    @GameTest(template = "3x2x1")
    public static void comparison_le(GameTestHelper helper) {
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);
        helper.setBlock(leftPos, Blocks.CHEST);
        helper.setBlock(rightPos, Blocks.CHEST);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var left = (ChestBlockEntity) helper.getBlockEntity(leftPos);
        var right = (ChestBlockEntity) helper.getBlockEntity(rightPos);
        var manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        left.setItem(0, new ItemStack(Items.DIAMOND, 64));
        left.setItem(1, new ItemStack(Items.DIAMOND, 64));
        left.setItem(2, new ItemStack(Items.IRON_INGOT, 12));
        right.setItem(0, new ItemStack(Items.STICK, 13));
        right.setItem(1, new ItemStack(Items.STICK, 64));
        right.setItem(2, new ItemStack(Items.DIRT, 1));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "comparison_le test"
                                   EVERY 20 TICKS DO
                                       IF left HAS le 10 diamond THEN
                                           -- should not happen
                                           INPUT diamond FROM left
                                           OUTPUT diamond TO right
                                       END
                                       IF left HAS <= 12 iron_ingot THEN
                                           -- should happen
                                           INPUT iron_ingot FROM left
                                           OUTPUT iron_ingot TO right
                                       END
                                       IF right HAS le 77 stick THEN
                                           -- should happen
                                           INPUT stick FROM right
                                           OUTPUT stick TO left
                                       END
                                       if right has <= 1 dirt then
                                           -- should happen
                                           input dirt from right
                                           output dirt to left
                                       end
                                   END
                                   """);

        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "left", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "right", helper.absolutePos(rightPos));

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            int leftDiamondCount = count(left, Items.DIAMOND);
            int leftIronCount = count(left, Items.IRON_INGOT);
            int leftStickCount = count(left, Items.STICK);
            int leftDirtCount = count(left, Items.DIRT);
            int rightDiamondCount = count(right, Items.DIAMOND);
            int rightIronCount = count(right, Items.IRON_INGOT);
            int rightStickCount = count(right, Items.STICK);
            int rightDirtCount = count(right, Items.DIRT);
            // the diamonds should have moved from left to right
            assertTrue(leftDiamondCount == 64 * 2, "left should have 128 diamonds");
            assertTrue(rightDiamondCount == 0, "right should have no diamonds");
            // the iron should have moved from left to right
            assertTrue(leftIronCount == 0, "left should have no iron ingots");
            assertTrue(rightIronCount == 12, "right should have 12 iron ingots");
            // the sticks should have moved from right to left
            assertTrue(rightStickCount == 0, "right should have no sticks");
            assertTrue(leftStickCount == 77, "left should have 77 sticks");
            // the dirt should have moved from right to left
            assertTrue(rightDirtCount == 0, "right should have no dirt");
            assertTrue(leftDirtCount == 1, "left should have 1 dirt");
        });
    }

}
