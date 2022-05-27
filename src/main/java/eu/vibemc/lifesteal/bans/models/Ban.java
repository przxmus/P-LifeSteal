package eu.vibemc.lifesteal.bans.models;

import java.util.UUID;

public class Ban {
    private UUID playerUUID;

    public Ban(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
}
