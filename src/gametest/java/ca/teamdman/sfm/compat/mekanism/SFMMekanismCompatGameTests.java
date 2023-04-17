package ca.teamdman.sfm.compat.mekanism;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMGameTestBase;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismInfuseTypes;
import mekanism.common.tier.ChemicalTankTier;
import mekanism.common.tile.TileEntityChemicalTank;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class SFMMekanismCompatGameTests extends SFMGameTestBase {
    @GameTest(template = "3x4x1")
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
                                   """);
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));


        // ensure it can move into an empty tank
        leftTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1_000_000L));
        rightTank.getInfusionTank().setStack(InfusionStack.EMPTY);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftTank.getInfusionTank().getStack().isEmpty(), "Contents did not depart");
            assertTrue(rightTank.getInfusionTank().getStack().getAmount() == 1_000_000L, "Contents did not arrive");
        });
    }

    @GameTest(template = "3x4x1")
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
                                   """);
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));


        // ensure it can move when there's already some in the destination
        leftTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1_000_000L));
        rightTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 1_000_000L));
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(leftTank.getInfusionTank().getStack().isEmpty(), "Contents did not depart");
            assertTrue(rightTank.getInfusionTank().getStack().getAmount() == 2_000_000L, "Contents did not arrive");
        });
    }

    @GameTest(template = "3x4x1")
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
                                   """);
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "a", helper.absolutePos(leftPos));
        SFMLabelNBTHelper.addLabel(manager.getDisk().get(), "b", helper.absolutePos(rightPos));

        // ensure it can move into a nearly full tank
        leftTank.getInfusionTank().setStack(new InfusionStack(MekanismInfuseTypes.REDSTONE.get(), 2_000_000L));
        rightTank.getInfusionTank().setStack(
                new InfusionStack(
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
}
