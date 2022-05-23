package eu.vibemc.lifesteal.commands;

import static eu.vibemc.lifesteal.commands.BansCommands.getAllBansCommands;
import static eu.vibemc.lifesteal.commands.HeartsCommands.getAllHeartsCommands;
import static eu.vibemc.lifesteal.commands.ItemsCommands.getAllItemsCommands;
import static eu.vibemc.lifesteal.commands.MainCommands.getMainCommands;

public class CommandsManager {
    public static void loadCommands() {
        getMainCommands()
                .withSubcommand(getAllBansCommands())
                .withSubcommand(getAllHeartsCommands())
                .withSubcommand(getAllItemsCommands())
                .register();
    }
}
