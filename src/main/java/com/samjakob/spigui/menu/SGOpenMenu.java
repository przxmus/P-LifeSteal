package com.samjakob.spigui.menu;

import com.samjakob.spigui.SGMenu;
import org.bukkit.entity.Player;

public class SGOpenMenu {

    private final SGMenu gui;
    private final Player player;

    public SGOpenMenu(final SGMenu gui, final Player player) {
        this.gui = gui;
        this.player = player;
    }

    public SGMenu getGUI() {
        return gui;
    }

    public Player getPlayer() {
        return player;
    }

}
