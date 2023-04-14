package ca.teamdman.sfm;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;

@SuppressWarnings("DataFlowIssue")
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMLaggyGameTests extends SFMGameTests {
    @GameTest(template = "25x3x25", batch = "laggy")
    public static void move_many_inventories(GameTestHelper helper) {
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

        assertManagerFirstTickSub1Second(helper, manager, () -> {
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

//            // remove all the items to speed up retests
//            destBlocks.forEach(pos -> {
//                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
//                for (int i = 0; i < barrel.getContainerSize(); i++) {
//                    barrel.setItem(i, ItemStack.EMPTY);
//                }
//            });
            helper.succeed();

        });
    }

    @GameTest(template = "25x3x25", batch = "laggy") //todo : fix whatever the heck is going on here
    public static void move_many_full(GameTestHelper helper) {
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

        assertManagerFirstTickSub1Second(helper, manager, () -> {
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

            helper.succeed();
        });
    }

    /**
     * Creates many inventories.
     * Half of them will be full, the other half will be empty.
     * The half that is full will have three different items, one type per row:
     * - iron ingots
     * - gold ingots
     * - diamonds
     * The program should use a regular expression to match only ingots in the form *:*_ingot
     */
    @GameTest(template = "25x3x25", batch = "laggy")
    public static void move_many_regex(GameTestHelper helper) {
        // fill the platform with cables and barrels
        var sourceBlocks = new ArrayList<BlockPos>();
        var destBlocks   = new ArrayList<BlockPos>();
        for (int x = 0; x < 25; x++) {
            for (int z = 0; z < 24; z++) { // make sure we have an even number to split
                // place a cable below
                helper.setBlock(new BlockPos(x, 2, z), SFMBlocks.CABLE_BLOCK.get());
                // place the barrel on top
                helper.setBlock(new BlockPos(x, 3, z), Blocks.BARREL);
                if (z % 2 == 0) {
                    sourceBlocks.add(new BlockPos(x, 3, z));
                } else {
                    destBlocks.add(new BlockPos(x, 3, z));
                }

                // fill the source chests with ingots
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(new BlockPos(x, 3, z));
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    if (z % 3 == 0) {
                        barrel.setItem(i, new ItemStack(Items.IRON_INGOT, 64));
                    } else if (z % 3 == 1) {
                        barrel.setItem(i, new ItemStack(Items.GOLD_INGOT, 64));
                    } else {
                        barrel.setItem(i, new ItemStack(Items.DIAMOND, 64));
                    }
                }
            }
        }

        // create the manager block and add the disk
        helper.setBlock(new BlockPos(0, 2, 0), SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(new BlockPos(0, 2, 0));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "many inventory regex test"
                                    
                    EVERY 20 TICKS DO
                        INPUT *:*_ingot FROM a
                        OUTPUT TO b
                    END
                """;

        // set the labels
        sourceBlocks.forEach(pos -> SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(pos)));
        destBlocks.forEach(pos -> SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(pos)));

        // load the program
        manager.setProgram(program);
        assertManagerFirstTickSub1Second(helper, manager, () -> {
            // ensure the source chests only have the non-ingot items
            sourceBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    if (pos.getZ() % 3 == 0) {
                        assertTrue(barrel.getItem(i).isEmpty(), "Items did not depart");
                    } else if (pos.getZ() % 3 == 1) {
                        assertTrue(barrel.getItem(i).isEmpty(), "Items did not depart");
                    } else {
                        assertTrue(barrel.getItem(i).getItem() == Items.DIAMOND, "Non-matching didn't stay");
                    }
                }
            });
            // ensure the destination chests only have the ingot items
            destBlocks.forEach(pos -> {
                BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getBlockEntity(pos);
                for (int i = 0; i < barrel.getContainerSize(); i++) {
                    if (pos.getZ() % 3 == 0) {
                        assertTrue(barrel.getItem(i).getItem() == Items.IRON_INGOT, "Items did not arrive");
                    } else if (pos.getZ() % 3 == 1) {
                        assertTrue(barrel.getItem(i).getItem() == Items.GOLD_INGOT, "Items did not arrive");
                    } else {
                        assertTrue(barrel.getItem(i).isEmpty(), "Only matching should have been received");
                    }
                }
            });
        });
    }
}
