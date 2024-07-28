package it.vamxneedev.strivelib.gui.elements;

import org.bukkit.entity.Player;

public interface InventoryProvider {

    void init(Player player, InventoryElements elements);
    default void update(Player player, InventoryElements elements) {}

}