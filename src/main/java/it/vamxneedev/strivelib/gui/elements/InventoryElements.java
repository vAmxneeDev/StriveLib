package it.vamxneedev.strivelib.gui.elements;

import it.vamxneedev.strivelib.gui.ClickableItem;
import it.vamxneedev.strivelib.gui.StriveInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface InventoryElements {
    StriveInventory inventory();
    Pagination pagination();

    Optional<SlotIterator> iterator(String id);

    SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn);
    SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn);

    SlotIterator newIterator(String id, SlotIterator.Type type, SlotPosition startPos);
    SlotIterator newIterator(SlotIterator.Type type, SlotPosition startPos);

    ClickableItem[][] all();

    Optional<SlotPosition> firstEmpty();

    Optional<ClickableItem> get(int row, int column);
    Optional<ClickableItem> get(SlotPosition SlotPosition);

    InventoryElements set(int row, int column, ClickableItem item);
    InventoryElements set(SlotPosition SlotPosition, ClickableItem item);

    InventoryElements add(ClickableItem item);

    InventoryElements fill(ClickableItem item);
    InventoryElements fillRow(int row, ClickableItem item);
    InventoryElements fillColumn(int column, ClickableItem item);
    InventoryElements fillBorders(ClickableItem item);

    InventoryElements fillRect(int fromRow, int fromColumn,
                               int toRow, int toColumn, ClickableItem item);
    InventoryElements fillRect(SlotPosition fromPos, SlotPosition toPos, ClickableItem item);

    <T> T property(String name);
    <T> T property(String name, T def);

    InventoryElements setProperty(String name, Object value);

    class Impl implements InventoryElements {
        private StriveInventory inventory;
        private UUID player;

        private ClickableItem[][] elements;

        private Pagination pagination = new Pagination.Impl();
        private Map<String, SlotIterator> iterators = new HashMap<>();
        private Map<String, Object> properties = new HashMap<>();

        public Impl(StriveInventory inventory, UUID player) {
            this.inventory = inventory;
            this.player = player;
            this.elements = new ClickableItem[inventory.getRows()][inventory.getColumns()];
        }

        @Override
        public StriveInventory inventory() { return inventory; }

        @Override
        public Pagination pagination() { return pagination; }

        @Override
        public Optional<SlotIterator> iterator(String id) {
            return Optional.ofNullable(this.iterators.get(id));
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
            SlotIterator iterator = new SlotIterator.Impl(this, inventory,
                    type, startRow, startColumn);

            this.iterators.put(id, iterator);
            return iterator;
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, SlotPosition startPos) {
            return newIterator(id, type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
            return new SlotIterator.Impl(this, inventory, type, startRow, startColumn);
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, SlotPosition startPos) {
            return newIterator(type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public ClickableItem[][] all() { return elements; }

        @Override
        public Optional<SlotPosition> firstEmpty() {
            for (int row = 0; row < elements.length; row++) {
                for(int column = 0; column < elements[0].length; column++) {
                    if(!this.get(row, column).isPresent())
                        return Optional.of(new SlotPosition(row, column));
                }
            }

            return Optional.empty();
        }

        @Override
        public Optional<ClickableItem> get(int row, int column) {
            if(row >= elements.length)
                return Optional.empty();
            if(column >= elements[row].length)
                return Optional.empty();

            return Optional.ofNullable(elements[row][column]);
        }

        @Override
        public Optional<ClickableItem> get(SlotPosition SlotPosition) {
            return get(SlotPosition.getRow(), SlotPosition.getColumn());
        }

        @Override
        public InventoryElements set(int row, int column, ClickableItem item) {
            if(row >= elements.length)
                return this;
            if(column >= elements[row].length)
                return this;

            elements[row][column] = item;
            update(row, column, item != null ? item.getItem() : null);
            return this;
        }

        @Override
        public InventoryElements set(SlotPosition SlotPosition, ClickableItem item) {
            return set(SlotPosition.getRow(), SlotPosition.getColumn(), item);
        }

        @Override
        public InventoryElements add(ClickableItem item) {
            for(int row = 0; row < elements.length; row++) {
                for(int column = 0; column < elements[0].length; column++) {
                    if(elements[row][column] == null) {
                        set(row, column, item);
                        return this;
                    }
                }
            }

            return this;
        }

        @Override
        public InventoryElements fill(ClickableItem item) {
            for(int row = 0; row < elements.length; row++)
                for(int column = 0; column < elements[row].length; column++)
                    set(row, column, item);

            return this;
        }

        @Override
        public InventoryElements fillRow(int row, ClickableItem item) {
            if(row >= elements.length)
                return this;

            for(int column = 0; column < elements[row].length; column++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryElements fillColumn(int column, ClickableItem item) {
            for(int row = 0; row < elements.length; row++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryElements fillBorders(ClickableItem item) {
            fillRect(0, 0, inventory.getRows() - 1, inventory.getColumns() - 1, item);
            return this;
        }

        @Override
        public InventoryElements fillRect(int fromRow, int fromColumn, int toRow, int toColumn, ClickableItem item) {
            for(int row = fromRow; row <= toRow; row++) {
                for(int column = fromColumn; column <= toColumn; column++) {
                    if(row != fromRow && row != toRow && column != fromColumn && column != toColumn)
                        continue;

                    set(row, column, item);
                }
            }

            return this;
        }

        @Override
        public InventoryElements fillRect(SlotPosition fromPos, SlotPosition toPos, ClickableItem item) {
            return fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name) {
            return (T) properties.get(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name, T def) {
            return properties.containsKey(name) ? (T) properties.get(name) : def;
        }

        @Override
        public InventoryElements setProperty(String name, Object value) {
            properties.put(name, value);
            return this;
        }

        private void update(int row, int column, ItemStack item) {
            Player currentPlayer = Bukkit.getPlayer(player);
            if(!inventory.getManager().getOpenedPlayers(inventory).contains(currentPlayer))
                return;

            Inventory topInventory = currentPlayer.getOpenInventory().getTopInventory();
            topInventory.setItem(inventory.getColumns() * row + column, item);
        }
    }
}
