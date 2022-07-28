package xyz.regulad.supermoderncombo.spigot;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import xyz.regulad.supermoderncombo.common.api.CommonAPI;
import xyz.regulad.supermoderncombo.common.db.MySQL;

import java.util.Objects;

public class SpigotPlugin extends JavaPlugin implements CommonAPI<Player> {
    @Getter
    private static @Nullable SpigotPlugin instance;
    @Getter
    private @Nullable Metrics metrics;
    @Getter
    private @Nullable MySQL<Player> mySQL;

    @Override
    public void onEnable() {
        // Setup instance access
        instance = this;
        CommonAPI.setInstance(this);
        // Setup config
        this.saveDefaultConfig();
        // Setup bStats metrics
        this.metrics = new Metrics(this, 15948); // TODO: Replace this in your plugin!
        // Setup MySQL
        if (this.getConfig().getBoolean("db.enabled", false)) {
            this.mySQL = new MySQL<>(
                    Objects.requireNonNull(this.getConfig().getString("db.host")),
                    this.getConfig().getInt("db.port"),
                    Objects.requireNonNull(this.getConfig().getString("db.database")),
                    Objects.requireNonNull(this.getConfig().getString("db.username")),
                    Objects.requireNonNull(this.getConfig().getString("db.password")),
                    this
            );
        }
        if (this.mySQL != null) {
            this.mySQL.setupTable();
            this.mySQL.connect();
        }
    }

    @Override
    public void onDisable() {
        // Discard instance access
        instance = null;
        CommonAPI.setInstance(null);
        // Discard bStats metrics
        this.metrics = null;
        // Discard MySQL
        if (this.mySQL != null) {
            this.mySQL.close();
        }
        this.mySQL = null;
    }
}
