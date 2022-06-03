package eu.vibemc.lifesteal.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import eu.vibemc.lifesteal.other.Items;
import org.bukkit.entity.Player;

public class ItemsCommands {
    public static CommandAPICommand getAllItemsCommands() {
        return new CommandAPICommand("give")
                .withShortDescription("Gives you specified item.")
                .withSubcommand(getGiveReviveBookCommand())
                .withSubcommand(getGiveExtraHeartCommand());
    }

    private static CommandAPICommand getGiveExtraHeartCommand() {
        return new CommandAPICommand("extra_heart")
                .withPermission("lifesteal.give.extraheart")
                .withArguments(new PlayerArgument("player"), new IntegerArgument("chance_of_success"), new IntegerArgument("amount")).executes((sender, args) -> {
                    Player player = (Player) args[0];
                    int chance = (int) args[1];
                    int amount = (int) args[2];
                    for (int i = 0; i < amount; i++) {
                        player.getInventory().addItem(Items.ExtraHeart.getExtraHeart(chance));
                        player.updateInventory();
                    }
                });
    }

    private static CommandAPICommand getGiveReviveBookCommand() {
        return new CommandAPICommand("revive_book")
                .withPermission("lifesteal.give.revivebook")
                .withArguments(new PlayerArgument("player"), new IntegerArgument("amount")).
                executes((sender, args) -> {
                    Player player = (Player) args[0];
                    int amount = (int) args[1];
                    for (int i = 0; i < amount; i++) {
                        player.getInventory().addItem(Items.ReviveBook.getReviveBook());
                        player.updateInventory();
                    }
                });
    }
}
