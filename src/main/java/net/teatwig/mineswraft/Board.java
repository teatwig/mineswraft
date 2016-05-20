package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by timo on 03.03.2016.
 */
class Board {
    private Field[][] board;
    @Getter(AccessLevel.PACKAGE)
    private int width, height, mines;
    @Getter(AccessLevel.PACKAGE)
    private boolean firstMoveDone = false;
    @Getter(AccessLevel.PACKAGE)
    private boolean gameOver = false, gameWon = false;
    @Getter(AccessLevel.PACKAGE)
    private int remainingMines;
    private LocalDateTime startTime;
    private int difficultyType = -1;

    Board(int width, int height, int mines, int difficultyType) {
        this(width, height, mines);
        this.difficultyType = difficultyType;
    }

    Board(int width, int height, int mines) { // currently only used in MainTerm because it doesnt specify a difficulty
        this.width = width; this.height = height; this.mines = mines;
        board = new Field[height][width];
        remainingMines = mines;
    }

    private void init(Coordinate startCoordinate) {
        init(startCoordinate, false);
    }

    public void initNonogramMode() {
        firstMoveDone = true;
        init(null, true); // could be 0 but value isn't used anyway
    }

    private void init(Coordinate startCoordinate, boolean nonogramModeEnabled) {
        List<Coordinate> minePositions = IntStream.range(0, width*height).mapToObj(p -> Coordinate.of(p%width, p/width)).collect(Collectors.toList());
        Collections.shuffle(minePositions);
        // init mines
        minePositions.stream()
                .filter(mineCoordinate -> nonogramModeEnabled || startCoordinate.isNotSurroundedBy(mineCoordinate))
                .limit(mines)
                .forEach(coordinate -> setField(coordinate, new Field.Mine()));
        // init remaining
        minePositions.stream()
                .filter(coordinate -> getField(coordinate) == null)
                .forEach(coordinate -> setField(coordinate, new Field(calcSurroundingMines(coordinate))));

        firstMoveDone = true;
        startTime = LocalDateTime.now();
    }

    private int calcSurroundingMines(Coordinate coordinate) {
        return coordinate.getStreamOfSurrounding().mapToInt(this::fieldIsMineToInt).sum();
    }

    private int fieldIsMineToInt(Coordinate coordinate) {
        Field f;
        try {
            f = getField(coordinate);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return 0;
        }
        return f==null ? 0 : (f.isMine() ? 1 : 0);
    }

    void click(Coordinate coordinate) {
        click(coordinate, false, false);
    }

    void toggleMarking(Coordinate coordinate) { click(coordinate, true, false); }

    void chord(Coordinate coordinate) {
        click(coordinate, false, true);
    }

    private void click(Coordinate coordinate, boolean mark, boolean chord) {
        if(!firstMoveDone) {
            init(coordinate);
        }
        if(mark) {
            try {
                getField(coordinate).toggleMarking();
                if(getField(coordinate).isMarked())
                    remainingMines -= 1;
                else
                    remainingMines += 1;
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
                // didn't handle possible errors down there either
            }
        } else if(chord && allPotentialSurroundingMarked(coordinate)) {
            openSurroundingMines(coordinate, true);
        } else {
            openSpace(coordinate);
        }

        if(onlyMinesLeft()) {
            remainingMines = 0;
            gameWon = true;
        }
    }

    private void openSpace(Coordinate coordinate) {
        openSpace(coordinate, false, false);
    }

    private void openSpace(Coordinate coordinate, boolean auto, boolean chord) {
        try {
            Field f = getField(coordinate);
            if(!f.isOpen() && !f.isMarked()) {
                if(!auto && f.isMine()) {
                    gameOver = true;
                    return;
                }

                if(!f.isMine() || chord) {
                    f.open();
                }

                if (f.getSurroundingMines() == 0) {
                    openSurroundingMines(coordinate);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            // everything's alright, yes
        }
    }

    private void openSurroundingMines(Coordinate coordinate) {
        openSurroundingMines(coordinate, false);
    }

    private void openSurroundingMines(Coordinate coordinate, boolean chord) {
        coordinate.getStreamOfSurrounding().forEach(surrCoordinate -> openSpace(surrCoordinate, false, chord));
    }

    private boolean allPotentialSurroundingMarked(Coordinate coordinate) {
        int surroundingMarked = coordinate.getStreamOfSurrounding().mapToInt(this::isMarkedTo1).sum();
        return surroundingMarked == getField(coordinate).getSurroundingMines();
    }

    private int isMarkedTo1(Coordinate coordinate) {
        try {
            if(getField(coordinate).isMarked())
                return 1;
        } catch (ArrayIndexOutOfBoundsException ex) {
            // another on of those
        }
        return 0;
    }

    private boolean onlyMinesLeft() {
        return Arrays.stream(board).flatMap(Arrays::stream).allMatch(f -> f.isOpen() || f.isMine());
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

    Field getField(int positionInFlatArray) {
        return board[positionInFlatArray/width][positionInFlatArray%width];
    }

    Field getField(Coordinate coordinate) {
        return board[coordinate.getY()][coordinate.getX()];
    }

    private void setField(Coordinate coordinate, Field field) {
        board[coordinate.getY()][coordinate.getX()] = field;
    }

    Field[] getRow(int n) {
        return board[n];
    }

    Field[] getColumn(int x) {
        return IntStream.range(0, height).mapToObj(y -> board[y][x]).toArray(Field[]::new);
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
