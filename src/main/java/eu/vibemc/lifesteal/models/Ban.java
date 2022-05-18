package eu.vibemc.lifesteal.models;

import java.util.UUID;

public class Ban {
    private UUID playerUUID;

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public Ban(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
}
