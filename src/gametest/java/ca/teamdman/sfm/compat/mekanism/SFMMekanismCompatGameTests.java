package ca.teamdman.sfm.compat.mekanism;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMGameTestBase;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.math.FloatingLong;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismInfuseTypes;
import mekanism.common.tier.BinTier;
import mekanism.common.tier.ChemicalTankTier;
import mekanism.common.tier.EnergyCubeTier;
import mekanism.common.tile.TileEntityBin;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "DuplicatedCode", "DataFlowIssue"})
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMMekanismCompatGameTests extends SFMGameTestBase {
    @GameTest(template = "3x2x1")
    public static void mek_chemtank_infusion_empty(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_CHEMICAL_TANK.getBlock());
        var leftTank = ((TileEntityChemicalTank) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_CHEMICAL_TANK.getBlock());
        var rightTank = ((TileEntityChemicalTank) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                      INPUT infusion:*:* FROM a NORTH SIDE -- mek can extract from front by default
                                      OUTPUT infusion:*:* TO b TOP SIDE -- mek can insert to top by default
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());


        // ensure it can move into an empty tank
        leftTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1_000_000L));
        rightTank.getInfusionTank().setStack(InfusionStack.EMPTY);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftTank.getInfusionTank().getStack().isEmpty(), "Contents did not depart");
            assertTrue(rightTank.getInfusionTank().getStack().getAmount() == 1_000_000L, "Contents did not arrive");
        });
    }

    @GameTest(template = "3x2x1")
    public static void mek_chemtank_infusion_some(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_CHEMICAL_TANK.getBlock());
        var leftTank = ((TileEntityChemicalTank) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_CHEMICAL_TANK.getBlock());
        var rightTank = ((TileEntityChemicalTank) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                      INPUT infusion:*:* FROM a NORTH SIDE -- mek can extract from front by default
                                      OUTPUT infusion:*:* TO b TOP SIDE -- mek can insert to top by default
                                   END
                                                           """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());


        // ensure it can move when there's already some in the destination
        leftTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1_000_000L));
        rightTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1_000_000L));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftTank.getInfusionTank().getStack().isEmpty(), "Contents did not depart");
            assertTrue(rightTank.getInfusionTank().getStack().getAmount() == 2_000_000L, "Contents did not arrive");
        });
    }

    @GameTest(template = "3x2x1")
    public static void mek_chemtank_infusion_full(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_CHEMICAL_TANK.getBlock());
        var leftTank = ((TileEntityChemicalTank) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_CHEMICAL_TANK.getBlock());
        var rightTank = ((TileEntityChemicalTank) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT infusion:*:* FROM a NORTH SIDE -- mek can extract from front by default
                                     OUTPUT infusion:*:* TO b TOP SIDE -- mek can insert to top by default
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        // ensure it can move into a nearly full tank
        leftTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 2_000_000L));
        rightTank
                .getInfusionTank()
                .setStack(new InfusionStack(
                        MekanismInfuseTypes.REDSTONE.get(),
                        ChemicalTankTier.ULTIMATE.getStorage() - 1_000_000L
                ));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftTank.getInfusionTank().getStack().getAmount() == 1_000_000L, "Contents did not depart");
            assertTrue(
                    rightTank.getInfusionTank().getStack().getAmount() == ChemicalTankTier.ULTIMATE.getStorage(),
                    "Contents did not arrive"
            );
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_bin_empty(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_BIN.getBlock());
        var left = ((TileEntityBin) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_BIN.getBlock());
        var right = ((TileEntityBin) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT FROM a NORTH SIDE
                                     OUTPUT TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.getBinSlot().setStack(new ItemStack(Items.COAL, BinTier.ULTIMATE.getStorage()));
        right.getBinSlot().setEmpty();
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(left.getBinSlot().getCount() == BinTier.ULTIMATE.getStorage() - 64, "Contents did not depart");
            assertTrue(right.getBinSlot().getCount() == 64, "Contents did not arrive");
            assertTrue(right.getBinSlot().getStack().getItem() == Items.COAL, "Contents wrong type");
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_bin_some(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_BIN.getBlock());
        var left = ((TileEntityBin) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_BIN.getBlock());
        var right = ((TileEntityBin) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT FROM a NORTH SIDE
                                     OUTPUT TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.getBinSlot().setStack(new ItemStack(Items.DIAMOND, 100));
        right.getBinSlot().setStack(new ItemStack(Items.DIAMOND, 100));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(left.getBinSlot().getCount() == 100 - 64, "Contents did not depart");
            assertTrue(right.getBinSlot().getCount() == 100 + 64, "Contents did not arrive");
            assertTrue(right.getBinSlot().getStack().getItem() == Items.DIAMOND, "Contents wrong type");
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_bin_full(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_BIN.getBlock());
        var left = ((TileEntityBin) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_BIN.getBlock());
        var right = ((TileEntityBin) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT FROM a NORTH SIDE
                                     OUTPUT TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.getBinSlot().setStack(new ItemStack(Items.STICK, BinTier.ULTIMATE.getStorage()));
        right.getBinSlot().setStack(new ItemStack(Items.STICK, BinTier.ULTIMATE.getStorage() - 32));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(left.getBinSlot().getCount() == BinTier.ULTIMATE.getStorage() - 32, "Contents did not depart");
            assertTrue(right.getBinSlot().getCount() == BinTier.ULTIMATE.getStorage(), "Contents did not arrive");
            assertTrue(right.getBinSlot().getStack().getItem() == Items.STICK, "Contents wrong type");
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_energy_empty(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var left = ((TileEntityEnergyCube) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var right = ((TileEntityEnergyCube) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT forge_energy:forge:energy FROM a NORTH SIDE
                                     OUTPUT forge_energy:forge:energy TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.setEnergy(0, EnergyCubeTier.ULTIMATE.getMaxEnergy());
        right.setEnergy(0, FloatingLong.ZERO);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(left.getEnergy(0).equals(FloatingLong.ZERO), "Contents did not depart");
            assertTrue(right.getEnergy(0).equals(EnergyCubeTier.ULTIMATE.getMaxEnergy()), "Contents did not arrive");
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_energy_some(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var left = ((TileEntityEnergyCube) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var right = ((TileEntityEnergyCube) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT forge_energy:forge:energy FROM a NORTH SIDE
                                     OUTPUT forge_energy:forge:energy TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.setEnergy(0, FloatingLong.create(1_000));
        right.setEnergy(0, FloatingLong.create(1_000));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(left.getEnergy(0).equals(FloatingLong.ZERO), "Contents did not depart");
            assertTrue(right.getEnergy(0).equals(FloatingLong.create(2_000)), "Contents did not arrive");
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_energy_full(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var left = ((TileEntityEnergyCube) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var right = ((TileEntityEnergyCube) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT forge_energy:forge:energy FROM a NORTH SIDE
                                     OUTPUT forge_energy:forge:energy TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.setEnergy(0, EnergyCubeTier.ULTIMATE.getMaxEnergy());
        right.setEnergy(0, EnergyCubeTier.ULTIMATE.getMaxEnergy().subtract(1_000));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(
                    left.getEnergy(0).equals(EnergyCubeTier.ULTIMATE.getMaxEnergy().subtract(1_000)),
                    "Contents did not depart"
            );
            assertTrue(right.getEnergy(0).equals(EnergyCubeTier.ULTIMATE.getMaxEnergy()), "Contents did not arrive");
            helper.succeed();
        });
    }


    @GameTest(template = "3x2x1")
    public static void mek_energy_one(GameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var left = ((TileEntityEnergyCube) helper.getBlockEntity(leftPos));
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        var right = ((TileEntityEnergyCube) helper.getBlockEntity(rightPos));
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT 1 forge_energy:forge:energy FROM a NORTH SIDE
                                     OUTPUT forge_energy:forge:energy TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk().get());

        left.setEnergy(0, FloatingLong.create(100));
        right.setEnergy(0, FloatingLong.ZERO);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(
                    left
                            .getEnergy(0)
                            .equals(FloatingLong
                                            .create(100)
                                            .subtract(UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(1))),
                    "Contents did not depart"
            );
            assertTrue(
                    right.getEnergy(0).equals(UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(1)),
                    "Contents did not arrive"
            );
            helper.succeed();
        });
    }


    @GameTest(template = "25x3x25")
    public static void mana_lava_cauldrons(GameTestHelper helper) {
        // designate positions
        var sourceBlocks = new ArrayList<BlockPos>();
        var destBlocks = new ArrayList<BlockPos>();
        var managerPos = new BlockPos(0, 2, 0);

        // set up cauldrons
        for (int x = 0; x < 25; x++) {
            for (int z = 1; z < 25; z++) {
                helper.setBlock(new BlockPos(x, 2, z), SFMBlocks.CABLE_BLOCK.get());
                helper.setBlock(new BlockPos(x, 3, z), Blocks.LAVA_CAULDRON);
                sourceBlocks.add(new BlockPos(x, 3, z));
            }
        }

        // set up tanks
        for (int i = 1; i < 25; i++) {
            BlockPos tankPos = new BlockPos(i, 2, 0);
            helper.setBlock(tankPos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
            destBlocks.add(tankPos);
        }

        // set up the manager
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "many inventory lag test"
                                    
                    EVERY 20 TICKS DO
                        INPUT fluid:*:* FROM source
                        OUTPUT fluid:*:* TO dest TOP SIDE
                    END
                """;

        // set the labels
        LabelPositionHolder.empty()
                .addAll("source", sourceBlocks.stream().map(helper::absolutePos).toList())
                .addAll("dest", destBlocks.stream().map(helper::absolutePos).toList())
                .save(manager.getDisk().get());

        // load the program
        manager.setProgram(program);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            sourceBlocks.forEach(pos -> helper.assertBlock(
                    pos,
                    Blocks.CAULDRON::equals,
                    () -> "Cauldron did not empty"
            ));
            int found = destBlocks
                    .stream()
                    .map(helper::absolutePos)
                    .map(pos -> helper.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, pos, Direction.DOWN))
                    .peek(Objects::requireNonNull)
                    .map(x -> x.getFluidInTank(0))
                    .mapToInt(FluidStack::getAmount)
                    .sum();
            assertTrue(found == 1000 * 25 * 24, "Not all fluids were moved (found " + found + ")");
            helper.succeed();

        });
    }

    @GameTest(template = "3x4x3")
    public static void multi_fluid(GameTestHelper helper) {
        var a1Pos = new BlockPos(2, 2, 1);
        var a2Pos = new BlockPos(1, 2, 0);
        var b1Pos = new BlockPos(1, 2, 2);
        var b2Pos = new BlockPos(0, 2, 1);
        var managerPos = new BlockPos(1, 2, 1);
        helper.setBlock(a1Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(a2Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(b1Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(b2Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        var a1 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(a1Pos), Direction.UP);
        var a2 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(a2Pos), Direction.UP);
        var b1 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(b1Pos), Direction.UP);
        var b2 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(b2Pos), Direction.UP);

        a1.fill(new FluidStack(Fluids.WATER, 3000), IFluidHandler.FluidAction.EXECUTE);
        a2.fill(new FluidStack(Fluids.LAVA, 3000), IFluidHandler.FluidAction.EXECUTE);

        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT fluid:: FROM a NORTH SIDE
                                     OUTPUT fluid::lava, fluid::water TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(a1Pos))
                .add("a", helper.absolutePos(a2Pos))
                .add("b", helper.absolutePos(b1Pos))
                .add("b", helper.absolutePos(b2Pos))
                .save(manager.getDisk().get());

        assertManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(a1.getFluidInTank(0).isEmpty(), "a1 did not empty");
            assertTrue(a2.getFluidInTank(0).isEmpty(), "a2 did not empty");
            assertTrue(b1.getFluidInTank(0).getFluid() == Fluids.WATER, "b1 did not fill with water");
            assertTrue(b2.getFluidInTank(0).getFluid() == Fluids.LAVA, "b2 did not fill with lava");
            helper.succeed();
        });
    }
}
