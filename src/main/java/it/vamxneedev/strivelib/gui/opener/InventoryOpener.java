package it.vamxneedev.strivelib.gui.opener;

import it.vamxneedev.strivelib.gui.ClickableItem;
import it.vamxneedev.strivelib.gui.StriveInventory;
import it.vamxneedev.strivelib.gui.elements.InventoryElements;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public interface InventoryOpener {
    Inventory open(StriveInventory inv, Player player);
    boolean supports(InventoryType type);

    default void fill(Inventory handle, InventoryElements elements) {
        ClickableItem[][] items = elements.all();

        for(int row = 0; row < items.length; row++) {
            for(int column = 0; column < items[row].length; column++) {
                if(items[row][column] != null)
                    handle.setItem(9 * row + column, items[row][column].getItem());
            }
        }
    }
}
