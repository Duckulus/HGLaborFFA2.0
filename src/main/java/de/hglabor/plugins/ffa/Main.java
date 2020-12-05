package de.hglabor.plugins.ffa;

import de.hglabor.plugins.ffa.config.Config;
import de.hglabor.plugins.ffa.gamemechanics.*;
import de.hglabor.plugins.ffa.kit.KitAbilityListener;
import de.hglabor.plugins.ffa.kit.KitItemListener;
import de.hglabor.plugins.ffa.kit.KitSelectorFFA;
import de.hglabor.plugins.ffa.listener.FFADeathListener;
import de.hglabor.plugins.ffa.listener.FFAJoinListener;
import de.hglabor.plugins.ffa.listener.FFAQuitListener;
import de.hglabor.plugins.ffa.player.FFAPlayer;
import de.hglabor.plugins.ffa.player.PlayerList;
import de.hglabor.plugins.ffa.util.ScoreboardFactory;
import de.hglabor.plugins.ffa.util.ScoreboardManager;
import de.hglabor.plugins.ffa.world.ArenaManager;
import de.hglabor.plugins.kitapi.config.KitApiConfig;
import de.hglabor.plugins.kitapi.kit.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private static ArenaManager arenaManager;
    private static FFARunnable ffaRunnable;

    public static FFARunnable getFFARunnable() {
        return ffaRunnable;
    }

    public static ArenaManager getArenaManager() {
        return arenaManager;
    }

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        KitApiConfig.getInstance().register(Main.getPlugin().getDataFolder());
        KitManager.getInstance().register();
        Config.load();
        World world = Bukkit.getWorld("world");
        arenaManager = new ArenaManager(world, Config.getInteger("ffa.size"));
        ffaRunnable = new FFARunnable(world, Config.getInteger("ffa.duration"));
        ffaRunnable.runTaskTimer(this, 0, 20);
        ScoreboardManager scoreboardManager = new ScoreboardManager();
        scoreboardManager.runTaskTimer(this, 0, 20);
        KitSelectorFFA.getInstance().register();
        this.registerListeners();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            FFAPlayer player = PlayerList.getInstance().getPlayer(onlinePlayer);
            PlayerList.getInstance().add(player);
            ScoreboardFactory.create(player);
            arenaManager.prepareKitSelection(onlinePlayer);
        }
    }

    @Override
    public void onDisable() {
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(KitSelectorFFA.getInstance(), this);
        pluginManager.registerEvents(new KitAbilityListener(), this);
        pluginManager.registerEvents(new KitItemListener(), this);
        pluginManager.registerEvents(new FFAJoinListener(), this);
        pluginManager.registerEvents(new FFAQuitListener(), this);
        pluginManager.registerEvents(new FFADeathListener(), this);
        //mechanics
        pluginManager.registerEvents(new SoupHealing(), this);
        pluginManager.registerEvents(new Tracker(), this);
        pluginManager.registerEvents(new DamageNerf(), this);
        pluginManager.registerEvents(new DurabilityFix(), this);
        pluginManager.registerEvents(new Feast(), this);
        pluginManager.registerEvents(new RemoveHitCooldown(), this);
    }
}
