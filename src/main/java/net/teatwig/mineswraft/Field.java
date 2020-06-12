package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by timo on 03.03.2016.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class Field {

    @Getter(AccessLevel.PACKAGE)
    private boolean isOpen = false;

    @Getter(AccessLevel.PACKAGE)
    private boolean isMarked = false;

    @Getter(AccessLevel.PACKAGE)
    private final boolean isMine;

    @Getter(AccessLevel.PACKAGE)
    private final int surroundingMines;

    Field(int surroundingMines) {
        this(false, surroundingMines);
    }

    void toggleMarking() {
        isMarked = !isMarked;
    }

    void open() {
        isOpen = true;
    }

    @Override
    public String toString() {
        if (isOpen()) {
            return getSurroundingMines() > 0 ? String.valueOf(getSurroundingMines()) : " ";
        } else {
            return isMarked() ? "Δ" : "#";
        }
    }

    static class Mine extends Field {

        Mine() {
            super(true, -1);
        }

    }

}
