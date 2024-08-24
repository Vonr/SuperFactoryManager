package ca.teamdman.sfm.common.command;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.export.ClientExportHelper;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.IOException;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = SFM.MOD_ID)
public class SFMCommand {
    @SubscribeEvent
    public static void onRegisterCommand(final RegisterCommandsEvent event) {
        var command = Commands.literal("sfm");
        command.then(Commands.literal("bust_cable_network_cache")
                             .requires(source -> source.hasPermission(0))
                             .executes(ctx -> {
                                 SFM.LOGGER.info(
                                         "Busting cable networks - slash command used by {}",
                                         ctx.getSource().getTextName()
                                 );
                                 CableNetworkManager.clear();
                                 return SINGLE_SUCCESS;
                             }));
        command.then(Commands.literal("show_bad_cable_cache_entries")
                             .requires(source -> source.hasPermission(2))
                             .then(Commands.argument("block", BlockStateArgument.block(event.getBuildContext()))
                                           .executes(ctx -> {
                                               ServerLevel level = ctx.getSource().getLevel();
                                               CableNetworkManager.getBadCableCachePositions(level).forEach(pos -> {
                                                   BlockInput block = BlockStateArgument
                                                           .getBlock(
                                                                   ctx,
                                                                   "block"
                                                           );
                                                   block.place(
                                                           level,
                                                           pos,
                                                           Block.UPDATE_ALL
                                                   );
                                               });
                                               return SINGLE_SUCCESS;
                                           })));
        if (FMLEnvironment.dist.isClient()) {
            command.then(Commands.literal("export_info")
                                 .requires(__ -> true)
                                 .executes(ctx -> {
                                     SFM.LOGGER.info(
                                             "Exporting info - slash command used by {}",
                                             ctx.getSource().getTextName()
                                     );
                                     try {
                                         ClientExportHelper.dumpItems(ctx.getSource().getPlayer());
                                         ClientExportHelper.dumpJei(ctx.getSource().getPlayer());
                                     } catch (IOException e) {
                                         SFM.LOGGER.error("Failed to export item data", e);
                                     }
                                     return SINGLE_SUCCESS;
                                 }));
        }
        event.getDispatcher().register(command);
    }

}
