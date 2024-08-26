package ca.teamdman.sfm.ai;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.gametest.SFMGameTestBase;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class OpenDoorTest extends SFMGameTestBase {
    @GameTest(template = "3x4x3", skyAccess = true)
    public static void open_door(GameTestHelper helper) {
        var pressurePlatePos = new BlockPos(0, 2, 1);
        var redstonePos = new BlockPos(1, 2, 1);
        var doorPos = new BlockPos(2, 2, 1);

        // set the floor to iron blocks
        for (int x = 0; x <= 2; x++) {
            for (int z = 0; z <= 2; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.IRON_BLOCK);
            }
        }

        // set the pressure plate
        helper.setBlock(pressurePlatePos, Blocks.OAK_PRESSURE_PLATE);

        // set the redstone dust
        helper.setBlock(redstonePos, Blocks.REDSTONE_WIRE);

        // set the door
        helper.setBlock(doorPos, Blocks.IRON_DOOR);
        
        var item = new ItemEntity(
                helper.getLevel(),
                redstonePos.getX(),
                redstonePos.getY(),
                redstonePos.getZ(),
                new ItemStack(Items.DIAMOND)
        );
        item.setDeltaMovement(0, 0, 0);

        // is this the right position? There might be an improvement here 👀
        item.setPos(Vec3.atCenterOf(helper.absolutePos(doorPos).offset(0, 1, 0)));
        // begin agent code
            item.setPos(Vec3.atCenterOf(helper.absolutePos(pressurePlatePos).offset(0, 1, 0)));
        // end agent code
        helper.getLevel().addFreshEntity(item);
        helper.succeedWhen(() -> helper.assertBlockProperty(doorPos, DoorBlock.OPEN, true));
    }
}
