package it.vamxneedev.strivelib.utilities.player;

import com.cryptomorin.xseries.XSound;
import it.vamxneedev.strivelib.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class PlayerUtils {
    public static Location getPlayerLocation(Player player) {
        return player.getLocation();
    }
    public static String getPlayerLocationString(Player player) {
        return String.format("X: %.2f, Y: %.2f, Z: %.2f", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
    }

    public static void addExperiencePoints(Player player, int amount) {
        player.giveExp(amount);
    }
    public static void removeExperiencePoints(Player player, int amount) {
        player.giveExp(-amount);
    }
    public static int getPlayerExperienceLevel(Player player) {
        return player.getLevel();
    }
    public static void setPlayerExperienceLevel(Player player, int level) {
        player.setLevel(level);
    }

    public static void increasePlayerHealth(Player player, double amount) {
        double currentHealth = player.getHealth();
        player.setHealth(currentHealth + amount);
    }
    public static void decreasePlayerHealth(Player player, double amount) {
        double currentHealth = player.getHealth();
        player.setHealth(currentHealth - amount);
    }

    public static void increasePlayerFoodLevel(Player player, int amount) {
        int currentFoodLevel = player.getFoodLevel();
        player.setFoodLevel(currentFoodLevel + amount);
    }
    public static void decreasePlayerFoodLevel(Player player, int amount) {
        int currentFoodLevel = player.getFoodLevel();
        player.setFoodLevel(currentFoodLevel - amount);
    }

    public static Location getPlayerSpawnLocation(Player player) {
        return player.getBedSpawnLocation() != null ? player.getBedSpawnLocation() : player.getWorld().getSpawnLocation();
    }
    public static void setPlayerSpawnLocation(Player player, Location location) {
        player.setBedSpawnLocation(location, true);
    }

    public static boolean isPlayerOnline(Player player) {
        return player.isOnline();
    }
    public static boolean isPlayerDead(Player player) {
        return player.isDead();
    }
    public static boolean isPlayerFlying(Player player) {
        return player.isFlying();
    }

    public static String getPlayerPermissionLevel(Player player) {
        return player.isOp() ? "op" : "default";
    }

    public static GameMode getGameMode(Player player) {
        return player.getGameMode();
    }
    public static void setGameMode(Player player, GameMode gameMode) {
        player.setGameMode(gameMode);
    }

    public static void giveItem(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack);
    }
    public static void removeItem(Player player, ItemStack itemStack) {
        player.getInventory().removeItem(itemStack);
    }
    public static void giveItemAll(ItemStack itemStack) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            giveItem(player, itemStack);
        }
    }
    public static void removeItemAll(ItemStack itemStack) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeItem(player, itemStack);
        }
    }

    public static void playSound(@Nonnull Player player, @Nonnull String sound, float volume, float pitch, String identifier) {
        try {
            Optional<XSound> soundType = XSound.matchXSound(sound.toUpperCase());
            if(!soundType.isPresent()){
                throw new IllegalArgumentException("&8[&b" + identifier + "&8] " + "Unknown sound: " + sound + "&8(&bERROR_SOUND_ARGS&8)");
            }

            Sound soundMatched = soundType.get().parseSound();
            assert soundMatched != null;
            player.playSound(player.getLocation(), soundMatched, volume, pitch);

        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("deprecated")
    public static ItemStack getMainHandItem(Player p) {
        if (Utils.versionToNumber() == 18)
            return p.getItemInHand();
        if (Utils.versionToNumber() > 18)
            return p.getInventory().getItemInMainHand();
        return p.getItemInHand();
    }

    public static int fetchFreeSlots(Player player) {
        Inventory inventory = player.getInventory();
        int contents = 0;

        for(ItemStack item : inventory.getContents()){
            if(item == null){
                contents++;
            }
        }
        return contents;
    }
    public static int fetchUsedSlots(Player player) {
        Inventory inventory = player.getInventory();
        int contents = 0;

        for(ItemStack item : inventory.getContents()){
            if(item != null){
                contents++;
            }
        }
        return contents;
    }
}
