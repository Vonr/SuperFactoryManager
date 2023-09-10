package ca.teamdman.sfm.ai;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMGameTestBase;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate(false)
public class TestChambers extends SFMGameTestBase {
    /**
     * The agent must make the test case pass.
     * The agent is provided code at the beginning and the end of the test case.
     */
    @GameTest(template = "3x4x3")
    public static void open_door(GameTestHelper helper) {
        // begin test prefix
        // This test case presents a wooden pressure plate, a redstone dust, and a door
        // The test passes when the door is detected as open
        // The code in the test prefix is the programmatic creation of the redstone, dust, door, and floor
        // The code in the test suffix is a test assertion that the blockstate of the door is open
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

        // did we place the diamond above the correct block?
        item.setPos(Vec3.atCenterOf(helper.absolutePos(doorPos).offset(0, 3, 0)));
        // end test prefix

        // begin agent code
            // Open the door by placing redstone dust
            helper.setBlock(redstonePos, Blocks.REDSTONE_WIRE);
            
            // Open the door by stepping on the pressure plate
            helper.setBlock(pressurePlatePos, Blocks.OAK_PRESSURE_PLATE);
            
            // Assert that the door is open
            helper.assertBlockProperty(doorPos, DoorBlock.OPEN, true);
            
        // end agent code

        // begin test suffix
        helper.getLevel().addFreshEntity(item);
        // succeed as soon as possible
        helper.succeedWhen(() -> {
            helper.assertBlockProperty(doorPos, DoorBlock.OPEN, true);
        });
        // shorten the timeout to 3 ticks
        helper.runAfterDelay(60, () -> {
            helper.assertBlockProperty(doorPos, DoorBlock.OPEN, true);
            helper.succeed();
        });
        // end test suffix
    }
}
