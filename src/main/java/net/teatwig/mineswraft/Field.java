package net.teatwig.mineswraft;

/**
 * Created by timo on 03.03.2016.
 */
public class Field {
    private boolean isOpen = false;
    private boolean isMine;
    private boolean isMarked = false;
    private final int surroundingMines;

    /**
     * Creates a new Field and flags it as isMine
     */
    public Field() {
        this.isMine = true;
        surroundingMines = -1;
    }

    /**
     * Creates a new non-mine Field with surroundingMines
     * @param surroundingMines
     */
    public Field(int surroundingMines) {
        this.isMarked = false;
        this.surroundingMines = surroundingMines;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void toggleMarking() {
        isMarked = !isMarked;
    }

    public void open() {
        isOpen = true;
    }

    public int getSurroundingMines() {
        return surroundingMines;
    }

    @Override
    public String toString() {
        if(isOpen()) {
            return getSurroundingMines()>0 ? String.valueOf(getSurroundingMines()) : " ";
        } else {
            if(isMarked())
                return "Δ";
            else
                return "#";
        }
    }
}
