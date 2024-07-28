package it.vamxneedev.strivelib.gui.elements;

public class SlotPosition {
    private final int row;
    private final int column;

    public SlotPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass())
            return false;

        SlotPosition slotPos = (SlotPosition) obj;

        return row == slotPos.row && column == slotPos.column;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;

        return result;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    public static SlotPosition of(int row, int column) {
        return new SlotPosition(row, column);
    }
}
