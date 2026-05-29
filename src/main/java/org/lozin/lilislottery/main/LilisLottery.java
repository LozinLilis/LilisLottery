package org.lozin.lilislottery.main;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.lozin.lilislottery.event.UiClose;
import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.lilislottery.lottery.recipe.Lucky;
import org.lozin.lilislottery.lottery.recipe.RequiredItem;
import org.lozin.lilislottery.ui.UiFactory;
import org.lozin.tool.file.FileService;
import org.lozin.tool.sound.Sounds;
import org.lozin.tool.string.Logger;
import org.lozin.tool.string.RegexService;

import java.io.File;

public final class LilisLottery extends JavaPlugin {
    @Getter private static LilisLottery instance;

    @Override
    public void onEnable() {
        instance = this;
        Logger.log(" ", "&f[&a√&f] 已启用 &9LilisLottery &8v"+getDescription().getVersion(), " ");
        saveDefaultResources(false, "config.yml", "lotteryGUI.yml");
        saveDefaultResources(true, "Recipes/example.yml");
        regPluginHandlers();
        RegexService.initPrecision();
        Lucky.regKey();
        RequiredItem.regDetectKey();
        FileService.regFacts(getDataFolder());
        Sounds.reg();
        RecipeCache.regAll();
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (UiFactory.getReg().containsKey(player.getUniqueId()) && player.getOpenInventory().getTopInventory().equals(UiFactory.getReg().get(player.getUniqueId()).getInventory())) {
                UiClose.itemBack(player, UiFactory.getReg().get(player.getUniqueId()).getInventory());
                player.closeInventory();
            }
        }
    }
    private void saveDefaultResources(boolean force, String... resources){
        for (String resource : resources) {
            File file = new File(getDataFolder(), resource);
            if (force) {
                saveResource(resource, true);
            }
            else if (!file.exists()) {
                saveResource(resource, false);
            }
        }
    }
    private void regPluginHandlers(){
        Bukkit.getPluginManager().registerEvents(new Eventer(), this);
        Bukkit.getPluginCommand("lilis_lot").setExecutor(new Commander());
        Bukkit.getPluginCommand("lilis_lot").setTabCompleter(new Taber());
    }
    public static boolean isDebug(){
        try {
            return getInstance().getConfig().getBoolean("debug");
        } catch (Exception e) {
            return false;
        }
    }
}
