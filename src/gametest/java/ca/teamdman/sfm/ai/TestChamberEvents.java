package ca.teamdman.sfm.ai;

import ca.teamdman.sfm.SFM;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

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

@SuppressWarnings("unused")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class TestChamberEvents {
    @Nullable
    private static Thread integrationServer = null;
    private static final ConcurrentHashMap<String, String> testResults = new ConcurrentHashMap<>();
    private static void announce(String message) {
        if (Minecraft.getInstance() == null || Minecraft.getInstance().player == null) {
            SFM.LOGGER.warn("Failed to announce to player: {}", message);
        } else {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(message));
        }
    }
    @SubscribeEvent
    public static void onClientSystemMessage(final ClientChatReceivedEvent.System event) {
        testResults.put("latest", event.getMessage().getString());
    }

    @SubscribeEvent
    public static void onRegisterCommand(final RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("sfm_ai");

        command.then(Commands.literal("listen")
                             .executes(ctx -> {
                                 if (integrationServer == null) {
                                     // spawn new thread
                                     integrationServer = new Thread(() -> {
                                         announce("Integration server starting up");
                                         Path filePath = Paths.get(
                                                 "D:\\Repos\\Minecraft\\Forge\\SuperFactoryManager\\ai\\templating\\messages\\run.txt");
//                                                 "D:\\Repos\\Minecraft\\Forge\\SuperFactoryManager\\ai\\rewrite\\messages\\run.txt");

                                         while (true) {  // Infinite loop to keep checking
                                             try {
                                                 if (Files.exists(filePath)) {
                                                     List<String> lines = Files.readAllLines(
                                                             filePath,
                                                             StandardCharsets.UTF_8
                                                     );

                                                     if (lines.isEmpty()) {  // If the file is empty
                                                         announce("Integration server starting test");

                                                         testResults.put("latest", "");

                                                         LocalPlayer player = Minecraft.getInstance().player;
                                                         player.setDeltaMovement(0, 0, 0);
                                                         player.setPos(0, -58, 0); // superflat
                                                         player.getAbilities().flying = true;
                                                         player.lookAt(
                                                                 EntityAnchorArgument.Anchor.EYES,
                                                                 Vec3.atCenterOf(new BlockPos(1, -58, 4))
                                                         );
                                                         player.connection.sendUnsignedCommand("test clearall");
                                                         Thread.sleep(100);
                                                         player.connection.sendUnsignedCommand("test run open_door");
                                                         String results;

                                                         while ((results = testResults.get("latest")).isEmpty()) {
                                                             announce(
                                                                     "Integration server waiting for test results");
                                                             Thread.sleep(100);
                                                         }

                                                         announce(
                                                                 "Integration server received test results " + results);

                                                         // Append results to the file
                                                         Files.write(
                                                                 filePath,
                                                                 results.getBytes(StandardCharsets.UTF_8),
                                                                 StandardOpenOption.APPEND
                                                         );
                                                     }
                                                 }

                                                 Thread.sleep(1000);  // Sleep for a second before checking again

                                             } catch (InterruptedException | IOException e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                     });
                                     integrationServer.setDaemon(true);
                                     integrationServer.start();
                                 } else {
                                     announce("Integration server already running");
                                 }
                                 return SINGLE_SUCCESS;
                             })
        );

        command.then(Commands.literal("stop")
                             .executes(ctx -> {
                                 if (integrationServer != null) {
                                     integrationServer.interrupt();
                                     integrationServer = null;
                                     announce("Integration server stopped");
                                 } else {
                                     announce("Integration server not running");
                                 }
                                 return SINGLE_SUCCESS;
                             })
        );

        SFM.LOGGER.info("Attaching test chamber commands");
        event.getDispatcher().register(command);
    }
}
