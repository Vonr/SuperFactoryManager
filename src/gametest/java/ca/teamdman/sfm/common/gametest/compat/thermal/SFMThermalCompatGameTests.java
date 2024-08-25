package ca.teamdman.sfm.common.gametest.compat.thermal;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.gametest.SFMGameTestBase;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "DataFlowIssue"})
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMThermalCompatGameTests extends SFMGameTestBase {

    @GameTest(template = "25x3x25")
    public static void thermal_furnace_array(GameTestHelper helper) {
        // designate positions
        var sourceBlocks = new ArrayList<BlockPos>();
        var destBlocks = new ArrayList<BlockPos>();
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
//                MachineFurnaceTile furnace = (MachineFurnaceTile) helper.getBlockEntity(new BlockPos(x, 3, z));
//                furnace.openGui()
                sourceBlocks.add(new BlockPos(x, 3, z));
            }
        }

        // set up destinations
        for (int i = 2; i < 3; i++) {
            BlockPos pos = new BlockPos(i, 2, 0);
            helper.setBlock(pos, Blocks.CHEST);
            destBlocks.add(pos);
        }

        // set up the manager
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = (ManagerBlockEntity) helper.getBlockEntity(managerPos);
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));

        // create the program
        var program = """
                    NAME "thermal furnace array test"
                    EVERY 20 TICKS DO
                        INPUT forge_energy:forge:energy FROM power NORTH SIDE
                        OUTPUT forge_energy:forge:energy TO source
                    END
                    EVERY 20 TICKS DO
                        INPUT FROM source
                        OUTPUT TO dest TOP SIDE
                    END
                """;

        // set the labels
        LabelPositionHolder.empty()
                .addAll("source", sourceBlocks.stream().map(helper::absolutePos).toList())
                .addAll("dest", destBlocks.stream().map(helper::absolutePos).toList())
                .add("power", powerPos)
                .save(manager.getDisk().get());

        // load the program
        manager.setProgram(program.stripIndent());
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
        });
    }
}
