package net.teatwig.mineswraft;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by timo on 15.04.2016.
 */
class NonogramNumbers {
    private List<List<Integer>> colNumbers, rowNumbers;

    NonogramNumbers(Board board) {
        colNumbers = new ArrayList<>();
        rowNumbers = new ArrayList<>();

        setColumnNumbers(board);
        setRowNumbers(board);
    }

    private void setColumnNumbers(Board board) {
        IntStream.range(0, board.getWidth()).forEach(no -> {
            colNumbers.add(new ArrayList<>());
            int currentNumber = 0;
            for(Field f : board.getColumn(no)) {
                if(f.isMine()) {
                    currentNumber++;
                } else if(!f.isMine()) {
                    if(currentNumber>0)
                        getLast(colNumbers).add(currentNumber);
                    currentNumber = 0;
                }
            }
            if(currentNumber>0) {
                getLast(colNumbers).add(currentNumber);
            }
        });
    }

    private void setRowNumbers(Board board) {
        IntStream.range(0, board.getWidth()).forEach(no -> {
            rowNumbers.add(new ArrayList<>());
            int currentNumber = 0;
            for(Field f : board.getRow(no)) {
                if(f.isMine()) {
                    currentNumber++;
                } else if(!f.isMine()) {
                    if(currentNumber>0)
                        getLast(rowNumbers).add(currentNumber);
                    currentNumber = 0;
                }
            }
            if(currentNumber>0) {
                getLast(rowNumbers).add(currentNumber);
            }
        });
    }

    GridPane getColNumberAsGridPane() {
        GridPane gridPane = new GridPane();
        for(int i = 0; i< colNumbers.size(); i++) {
            if(colNumbers.get(i).isEmpty()) {
                Button b = new Button();
                b.setVisible(false);
                gridPane.add(b, i, 0);
            }
            for(int j = 0; j< colNumbers.get(i).size(); j++) {
                int deltaSize = getXNumMaxSize() - colNumbers.get(i).size();
                gridPane.add(new Button(colNumbers.get(i).get(j).toString()), i, j+deltaSize);
            }
        }
        applyButtonStyles(gridPane, true);
        return gridPane;
    }

    GridPane getRowNumberAsGridPane() {
        GridPane gridPane = new GridPane();
        for(int i = 0; i< rowNumbers.size(); i++) {
            if(rowNumbers.get(i).isEmpty()) {
                Button b = new Button();
                b.setVisible(false);
                gridPane.add(b, 0, i);
            }
            for(int j = 0; j< rowNumbers.get(i).size(); j++) {
                int deltaSize = getYNumMaxSize() - rowNumbers.get(i).size();
                gridPane.add(new Button(rowNumbers.get(i).get(j).toString()), j+deltaSize, i);
            }
        }
        applyButtonStyles(gridPane, false);
        return gridPane;
    }

    private void applyButtonStyles(GridPane gridPane, boolean xNumbers) {
        gridPane.getChildren().stream().forEach(child -> {
            Button b = (Button) child;
            b.setPrefSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setMinSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setMaxSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setPadding(Insets.EMPTY);
            b.setFont(Font.font(null, FontWeight.NORMAL, 16));
            b.setStyle("-fx-border-style: "+(xNumbers?"hidden solid":"solid hidden")+
                    "; -fx-border-color: #DDD; -fx-border-size: 1px; -fx-background-radius: 0; -fx-background-color: #EFEFEF");
        });
    }

    private int getXNumMaxSize() {
        return getSize(colNumbers);
    }

    private int getYNumMaxSize() {
        return getSize(rowNumbers);
    }

    private <T> int getSize(List<List<T>> lists) {
        return lists.stream().mapToInt(List::size).max().getAsInt();
    }

    private static Field[][] flipDiagonally(Field[][] fields) {
        Field[][] flipFields = new Field[fields[0].length][fields.length];
        for(int i=0; i<fields.length; i++) {
            for(int j=0; j<fields[0].length; j++) {
                flipFields[j][i] = fields[i][j];
            }
        }
        return flipFields;
    }

    private static <T> T getLast(List<T> list) {
        return list.get(list.size()-1);
    }
}
