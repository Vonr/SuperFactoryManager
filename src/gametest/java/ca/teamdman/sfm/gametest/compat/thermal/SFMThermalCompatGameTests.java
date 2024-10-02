package ca.teamdman.sfm.gametest.compat.thermal;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.gametest.SFMGameTestBase;
import cofh.thermal.expansion.block.entity.machine.MachineFurnaceTile;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "DataFlowIssue"})
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMThermalCompatGameTests extends SFMGameTestBase {

    @GameTest(template = "25x3x25", timeoutTicks = 20 * 20)
    public static void thermal_furnace_array(GameTestHelper helper) {
        // designate positions
        var furnacePositions = new ArrayList<BlockPos>();
        var resultChestPositions = new ArrayList<BlockPos>();
        var ingredientChestPositions = new ArrayList<BlockPos>();
        var managerPos = new BlockPos(0, 2, 0);
        var powerPos = new BlockPos(1, 2, 0);

        // set up power
        helper.setBlock(powerPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        helper
                .getBlockEntity(powerPos)
                .getCapability(ForgeCapabilities.ENERGY, Direction.UP)
                .ifPresent(energy -> energy.receiveEnergy(Integer.MAX_VALUE, false));

        // set up furnaces
        var furnaceBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thermal", "machine_furnace"));
        for (int x = 0; x < 25; x++) {
            for (int z = 1; z < 25; z++) {
                helper.setBlock(new BlockPos(x, 2, z), SFMBlocks.CABLE_BLOCK.get());
                helper.setBlock(new BlockPos(x, 3, z), furnaceBlock);
                furnacePositions.add(new BlockPos(x, 3, z));
                var furnace = (MachineFurnaceTile) helper.getBlockEntity(new BlockPos(x, 3, z));
                furnace.setSideConfig(Direction.UP, MachineFurnaceTile.SideConfig.SIDE_INPUT);
                furnace.setSideConfig(Direction.DOWN, MachineFurnaceTile.SideConfig.SIDE_OUTPUT);
            }
        }

        // set up destinations
        for (int i = 2; i <= 3; i++) {
            BlockPos pos = new BlockPos(i, 2, 0);
            helper.setBlock(pos, SFMBlocks.TEST_BARREL_BLOCK.get());
            resultChestPositions.add(pos);
        }

        // set up ingredients
        for (int i = 5; i <= 6; i++) {
            BlockPos pos = new BlockPos(i, 2, 0);
            helper.setBlock(pos, SFMBlocks.TEST_BARREL_BLOCK.get());
            ingredientChestPositions.add(pos);
            for (int slot = 0; slot < 27; slot++) {
                getItemHandler(helper, pos).insertItem(slot, new ItemStack(Items.CHICKEN, 64), false);
            }
        }

        // set up the manager
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "thermal furnace array test"
                    EVERY 5 TICKS DO
                        INPUT forge_energy:forge:energy FROM power NORTH SIDE
                        OUTPUT forge_energy:forge:energy TO furnaces
                    END
                    EVERY 20 TICKS DO
                        INPUT FROM ingredients
                        OUTPUT RETAIN 2 TO EACH furnaces TOP SIDE
                    FORGET
                        INPUT FROM furnaces BOTTOM SIDE
                        OUTPUT TO results TOP SIDE
                    END
                """;

        // set the labels
        LabelPositionHolder.empty()
                .addAll("furnaces", furnacePositions.stream().map(helper::absolutePos).toList())
                .addAll("ingredients", ingredientChestPositions.stream().map(helper::absolutePos).toList())
                .addAll("results", resultChestPositions.stream().map(helper::absolutePos).toList())
                .add("power", helper.absolutePos(powerPos))
                .save(manager.getDisk().get());

        // load the program
        manager.setProgram(program.stripIndent());
        helper.succeedWhen(() -> {
            // the result chests must be full of cooked chicken
            for (BlockPos resultChestPosition : resultChestPositions) {
                boolean hasEnoughChicken = count(getItemHandler(helper, resultChestPosition), Items.COOKED_CHICKEN) >= 64 * 27;
                if (!hasEnoughChicken) {
                    helper.fail("Not enough cooked chicken in chest at " + resultChestPosition);
                }
            }
            helper.succeed();
        });
    }
}
