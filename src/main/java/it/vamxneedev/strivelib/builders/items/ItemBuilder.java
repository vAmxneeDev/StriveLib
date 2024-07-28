package it.vamxneedev.strivelib.builders.items;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.vamxneedev.strivelib.utilities.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder {

    private Material material;
    private int amount;
    private String displayName;
    private byte data;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private List<ItemFlag> flags;
    private Color leatherColor;
    private Integer customModelData;
    private String base64;
    private String headNameP;

    public ItemBuilder(Material material) {
        this.material = material;
        this.data = 0;
        this.amount = 1;
        this.displayName = null;
        this.lore = new ArrayList<>();
        this.enchants = null;
        this.flags = new ArrayList<>();
        this.leatherColor = null;
        this.customModelData = null;
        this.base64 = null;
        this.headNameP = null;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        this.displayName = Utils.colorize(displayName);
        return this;
    }

    public ItemBuilder setData(byte data) {
        this.data = data;
        return this;
    }

    public ItemBuilder playerHead(String headNameP) {
        this.headNameP = headNameP;
        return this;
    }

    public ItemBuilder base64(String base64) {
        this.base64 = base64;
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = Utils.colorizeList(lore);
        return this;
    }

    public ItemBuilder addLore(String loreLine) {
        this.lore.add(Utils.colorize(loreLine));
        return this;
    }

    public ItemBuilder enchants(Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        if (this.enchants == null) {
            this.enchants = new HashMap<>();
        }
        this.enchants.put(enchantment, level);
        return this;
    }

    public ItemBuilder flags(List<ItemFlag> flags) {
        this.flags = flags;
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public ItemBuilder leatherColor(Color color) {
        if (material == Material.LEATHER_BOOTS || material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_HELMET || material == Material.LEATHER_LEGGINGS) {
            this.leatherColor = color;
        }
        return this;
    }

    public ItemBuilder customModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack;
        if(this.data != 0)
            itemStack = new ItemStack(material, amount, data);
        else
            itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();

        if (displayName != null) {
            assert meta != null;
            meta.setDisplayName(displayName);
        }

        if (!lore.isEmpty()) {
            assert meta != null;
            meta.setLore(lore);
        }

        if (enchants != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                assert meta != null;
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        for (ItemFlag flag : flags) {
            assert meta != null;
            meta.addItemFlags(flag);
        }

        if (leatherColor != null && meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            leatherMeta.setColor(leatherColor);
        }

        if (customModelData != null) {
            assert meta != null;
            meta.setCustomModelData(customModelData);
        }

        if(itemStack == XMaterial.PLAYER_HEAD.parseItem() && headNameP != null) {
            SkullMeta skullMeta = (SkullMeta) meta;
            assert skullMeta != null;
            skullMeta.setOwner(headNameP);
            itemStack.setItemMeta(skullMeta);
        }

        if(itemStack == XMaterial.PLAYER_HEAD.parseItem() && base64 != null) {
            SkullMeta skullMeta = (SkullMeta) meta;
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", base64));

            Field profileField = null;
            try {
                assert meta != null;
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }

            itemStack.setItemMeta(skullMeta);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
