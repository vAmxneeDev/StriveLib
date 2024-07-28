package it.vamxneedev.strivelib.builders.items;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionBuilder extends ItemBuilder {
    private final PotionEffectType potionEffectType;
    private int duration;
    private int amplifier;

    public PotionBuilder(PotionEffectType potionEffectType) {
        super(XMaterial.POTION.parseMaterial());
        this.potionEffectType = potionEffectType;
        this.duration = 1;
        this.amplifier = 0;
    }

    public PotionBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public PotionBuilder amplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    @Override
    public ItemStack build() {
        ItemStack potion = super.build();
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        assert meta != null;
        meta.addCustomEffect(new PotionEffect(potionEffectType, duration, amplifier), false);

        potion.setItemMeta(meta);
        return potion;
    }
}
