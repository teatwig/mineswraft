package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by timo on 03.03.2016.
 */
class Field {
    @Getter(AccessLevel.PACKAGE)
    private boolean isOpen = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean isMarked = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean isMine;
    @Getter(AccessLevel.PACKAGE)
    private final int surroundingMines;

    /**
     * Creates a new Field and flags it as isMine
     */
    Field() {
        this.isMine = true;
        surroundingMines = -1;
    }

    /**
     * Creates a new non-mine Field with surroundingMines
     * @param surroundingMines
     */
    Field(int surroundingMines) {
        this.isMarked = false;
        this.surroundingMines = surroundingMines;
    }

    void toggleMarking() {
        isMarked = !isMarked;
    }

    void open() {
        isOpen = true;
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
