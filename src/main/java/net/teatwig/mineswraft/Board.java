package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.teatwig.mineswraft.Achievement.*;

/**
 * Created by timo on 03.03.2016.
 */
class Board {
    private Field[][] board;
    private int width, height, mines;
    @Getter(AccessLevel.PACKAGE)
    private boolean firstMoveDone = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean gameOver = false, gameWon = false;
    @Getter(AccessLevel.PACKAGE)
    private int remainingMines;
    private LocalDateTime startTime;
    private int difficultyType = -1;
    @Getter(AccessLevel.PACKAGE)
    private Set<Achievement> newAchievements = new HashSet<>();
    private boolean noFieldMarked = true; // Achievement

    Board(int width, int height, int mines, int difficultyType) {
        this(width, height, mines);
        this.difficultyType = difficultyType;
    }

    Board(int width, int height, int mines) {
        this.width = width; this.height = height; this.mines = mines;
        board = new Field[height][width];
        remainingMines = mines;
    }

    private void init(int x_start, int y_start) {
        init(x_start, y_start, false);
    }

    private void initNonogramMode() {
        init(-3, -3, true); // could be 0 but value isn't used anyway
    }

    private void init(int x_start, int y_start, boolean nonogramModeEnabled) {
        List<Integer> minePositions = IntStream.range(0, width*height).boxed().collect(Collectors.toList());
        Collections.shuffle(minePositions);
        // init mines
        minePositions.stream()
                .map(p -> new int[]{p%width, p/width})
                .filter(arr -> nonogramModeEnabled || notAdjacentToOrStart(x_start, y_start, arr[0], arr[1]))
                .limit(mines)
                .forEach(arr -> board[arr[1]][arr[0]] = new Field());
        // init remaining
        for (int y_cord=0; y_cord<height; y_cord++) {
            for (int x_cord=0; x_cord<width; x_cord++) {
                if(board[y_cord][x_cord] == null) { // isn't already init as mine
                    board[y_cord][x_cord] = new Field(calcSurroundingMines(x_cord, y_cord));
                }
            }
        }
        firstMoveDone = true;
        startTime = LocalDateTime.now();
    }


    private boolean notAdjacentToOrStart(int x_start, int y_start, int x_cord, int y_cord) {
        return x_cord < x_start - 1 || x_cord > x_start + 1 || y_cord < y_start - 1 || y_cord > y_start + 1;
    }

    private int calcSurroundingMines(int x_cord, int y_cord) {
        int surroundingMines = 0;
        surroundingMines += fieldIsMineToInt(x_cord-1, y_cord-1);
        surroundingMines += fieldIsMineToInt(x_cord,   y_cord-1);
        surroundingMines += fieldIsMineToInt(x_cord+1, y_cord-1);
        surroundingMines += fieldIsMineToInt(x_cord-1, y_cord);
        surroundingMines += fieldIsMineToInt(x_cord+1, y_cord);
        surroundingMines += fieldIsMineToInt(x_cord-1, y_cord+1);
        surroundingMines += fieldIsMineToInt(x_cord,   y_cord+1);
        surroundingMines += fieldIsMineToInt(x_cord+1, y_cord+1);
        return surroundingMines;
    }

    private int fieldIsMineToInt(int x_cord, int y_cord) {
        Field f;
        try {
            f = board[y_cord][x_cord];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return 0;
        }
        return f==null ? 0 : (f.isMine() ? 1 : 0);
    }

    void click(int x_cord, int y_cord) {
        click(x_cord, y_cord, false, false);
    }

    void toggleMarking(int x_cord, int y_cord) { click(x_cord, y_cord, true, false); }

    void chord(int x_cord, int y_cord) {
        click(x_cord, y_cord, false, true);
    }

    private void click(int x_cord, int y_cord, boolean mark, boolean chord) {
        if(!firstMoveDone) {
            init(x_cord, y_cord);
        }
        if(mark) {
            try {
                board[y_cord][x_cord].toggleMarking();
                if(board[y_cord][x_cord].isMarked())
                    remainingMines -= 1;
                else
                    remainingMines += 1;

                noFieldMarked = false;
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
                // didn't handle possible errors down there either
            }
        } else if(chord && allPotentialSurroundingMarked(x_cord, y_cord)) {
            openSurroundingMines(x_cord, y_cord, true);
        } else {
            openSpace(x_cord, y_cord);
        }

        if(onlyMinesLeft()) {
            remainingMines = 0;
            gameWon = true;

            handleAchievements();
        }
    }

    private void openSpace(int x_cord, int y_cord) {
        openSpace(x_cord, y_cord, false, false);
    }

    private void openSpace(int x_cord, int y_cord, boolean auto, boolean chord) {
        try {
            Field f = board[y_cord][x_cord];
            if(f.isOpen()) {
                return;
            } else if(!f.isMarked()) {
                if(!auto && f.isMine()) {
                    gameOver = true;
                    return;
                }

                if(!f.isMine() || chord) {
                    f.open();
                }

                if (f.getSurroundingMines() == 0) {
                    openSurroundingMines(x_cord, y_cord);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            // everything's alright, yes
        }
    }

    private void openSurroundingMines(int x_cord, int y_cord) {
        openSurroundingMines(x_cord, y_cord, false);
    }

    private void openSurroundingMines(int x_cord, int y_cord, boolean chord) {
        openSpace(x_cord-1, y_cord-1, false, chord);
        openSpace(x_cord,   y_cord-1, false, chord);
        openSpace(x_cord+1, y_cord-1, false, chord);
        openSpace(x_cord-1, y_cord,   false, chord);
        openSpace(x_cord+1, y_cord,   false, chord);
        openSpace(x_cord-1, y_cord+1, false, chord);
        openSpace(x_cord,   y_cord+1, false, chord);
        openSpace(x_cord+1, y_cord+1, false, chord);
    }

    private boolean allPotentialSurroundingMarked(int x_cord, int y_cord) {
        int surroundingMarked = 0;

        surroundingMarked += isMarkedTo1(x_cord-1, y_cord-1);
        surroundingMarked += isMarkedTo1(x_cord,   y_cord-1);
        surroundingMarked += isMarkedTo1(x_cord+1, y_cord-1);
        surroundingMarked += isMarkedTo1(x_cord-1, y_cord);
        surroundingMarked += isMarkedTo1(x_cord+1, y_cord);
        surroundingMarked += isMarkedTo1(x_cord-1, y_cord+1);
        surroundingMarked += isMarkedTo1(x_cord,   y_cord+1);
        surroundingMarked += isMarkedTo1(x_cord+1, y_cord+1);

        return surroundingMarked == board[y_cord][x_cord].getSurroundingMines();
    }

    private int isMarkedTo1(int x_cord, int y_cord) {
        try {
            if(board[y_cord][x_cord].isMarked())
                return 1;
        } catch (ArrayIndexOutOfBoundsException ex) {
            // another on of those
        }
        return 0;
    }

    private boolean onlyMinesLeft() {
        for(Field[] f_arr : board) {
            for(Field f : f_arr) {
                if(!f.isOpen() && !f.isMine()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleAchievements() {
        handleAchievementsWin();
        handleAchievementsNoMark();
    }

    private void handleAchievementsWin() {
        switch (difficultyType) {
            case Difficulty.EASY:
                EASY_WIN.addToIfNotObtained(newAchievements);
                break;
            case Difficulty.MEDIUM:
                MEDIUM_WIN.addToIfNotObtained(newAchievements);
                break;
            case Difficulty.HARD:
                HARD_WIN.addToIfNotObtained(newAchievements);
                break;
        }
    }

    private void handleAchievementsNoMark() {
        if(noFieldMarked) {
            switch (difficultyType) {
                case Difficulty.EASY:
                    EASY_NOMARK.addToIfNotObtained(newAchievements);
                    break;
                case Difficulty.MEDIUM:
                    MEDIUM_NOMARK.addToIfNotObtained(newAchievements);
                    break;
                case Difficulty.HARD:
                    HARD_NOMARK.addToIfNotObtained(newAchievements);
                    break;
            }
        }
    }

    boolean isGameInProgress() {
        return firstMoveDone && !gameOver && !gameWon;
    }

    Duration getTimePassed() {
        if(startTime == null) {
            return Duration.ZERO;
        } else {
            return Duration.between(startTime, LocalDateTime.now());
        }
    }

    Field[][] getFields() {
        return board;
    }

    Field getField(int x_cord, int y_cord) {
        return board[y_cord][x_cord];
    }

    public String xRayBoardToString() {
        StringBuilder boardString = new StringBuilder();
        boardString.append("o ");
        IntStream.range(0, width).forEach(i -> boardString.append("- "));
        boardString.append("o\n");
        for (Field[] f_a : board) {
            boardString.append("| ");
            for (Field f : f_a) {
                boardString.append(f.isMine()?"X ":f.getSurroundingMines()+" ");
            }
            boardString.append("|\n");
        }
        boardString.append("o ");
        IntStream.range(0, width).forEach(i -> boardString.append("- "));
        boardString.append("o");
        return boardString.toString();
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        boardString.append("o ");
        IntStream.range(0, width).forEach(i -> boardString.append("- "));
        boardString.append("o\n");
        for (Field[] f_a : board) {
            boardString.append("| ");
            for (Field f : f_a) {
                boardString.append(f==null?"# ":f+" ");
            }
            boardString.append("|\n");
        }
        boardString.append("o ");
        IntStream.range(0, width).forEach(i -> boardString.append("- "));
        boardString.append("o");
        return boardString.toString();
    }
}
