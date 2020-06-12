package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created by timo on 24.03.2016.
 */
class Difficulty {

    @Getter(AccessLevel.PACKAGE)
    private int type;

    @Getter(AccessLevel.PACKAGE)
    private int width, height, mines;

    static final int EASY = 0, MEDIUM = 1, HARD = 2, CUSTOM = 3;

    Difficulty(String name, int width, int height, int mines) {
        this(width, height, mines);
        this.type = parseName(name);
    }

    private Difficulty(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;
    }

    private int parseName(String name) {
        switch (name) {
            case "Easy":
                return EASY;
            case "Medium":
                return MEDIUM;
            case "Hard":
                return HARD;
            case "Custom":
                return CUSTOM;
            default:
                return -1;
        }
    }

}
