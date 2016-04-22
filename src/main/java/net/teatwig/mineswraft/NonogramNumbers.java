package net.teatwig.mineswraft;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by timo on 15.04.2016.
 */
class NonogramNumbers {
    private List<List<Integer>> colNumbers, rowNumbers;

    NonogramNumbers(Board board) {
        setColNumbers(board);
        setRowNumbers(board);
    }

    private void setColNumbers(Board board) {
        colNumbers = new ArrayList<>();
        IntStream.range(0, board.getWidth()).forEach(no -> addNumbersToFromColOrRow(colNumbers, board.getColumn(no)));
    }

    private void setRowNumbers(Board board) {
        rowNumbers = new ArrayList<>();
        IntStream.range(0, board.getWidth()).forEach(no -> addNumbersToFromColOrRow(rowNumbers, board.getRow(no)));
    }

    private void addNumbersToFromColOrRow(List<List<Integer>> numbers, Field[] arr) {
        numbers.add(new ArrayList<>());
        int currentNumber = 0;
        if(Arrays.stream(arr).noneMatch(Field::isMine)) {
            getLast(numbers).add(0);
        } else {
            for (Field f : arr) {
                if (f.isMine()) {
                    currentNumber++;
                } else if (!f.isMine()) {
                    if (currentNumber > 0)
                        getLast(numbers).add(currentNumber);
                    currentNumber = 0;
                }
            }
            if (currentNumber > 0) {
                getLast(numbers).add(currentNumber);
            }
        }
    }

    GridPane getColNumberAsGridPane() {
        return getGridPaneFor(colNumbers, true);
    }

    GridPane getRowNumberAsGridPane() {
        return getGridPaneFor(rowNumbers, false);
    }

    private GridPane getGridPaneFor(List<List<Integer>> numbers, boolean columns) {
        GridPane gridPane = new GridPane();
        IntStream.range(0, numbers.size()).forEach(i -> {
            if(columns)
                gridPane.addColumn(i, getButtons(numbers, i, columns));
            else
                gridPane.addRow(i, getButtons(numbers, i, columns));
        });
        return gridPane;
    }

    private Node[] getButtons(List<List<Integer>> numbers, int i, boolean columns) {
        Button[] buttons =  Stream.concat(
            IntStream.range(0, getMaxSublistSize(numbers)-numbers.get(i).size()).mapToObj(emptyButton),
            numbers.get(i).stream().map(String::valueOf).map(Button::new)
        ).toArray(Button[]::new);

        Arrays.stream(buttons).forEach(b -> {
            b.setPrefSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setMinSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setMaxSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setPadding(Insets.EMPTY);
            b.setFont(Font.font(null, FontWeight.NORMAL, 16));
            b.setStyle("-fx-border-style: "+(columns?"hidden solid":"solid hidden")+
                    "; -fx-border-color: #DDD; -fx-border-size: 1px; -fx-background-radius: 0; -fx-background-color: #EFEFEF");
            if(b.getText().equals("0")) {
                b.setTextFill(Color.valueOf("gray"));
            }
        });

        return buttons;
    }

    private IntFunction<Button> emptyButton = i -> {
        Button b = new Button();
        b.setVisible(false);
        return b;
    };

    private <T> int getMaxSublistSize(List<List<T>> lists) {
        return lists.stream().mapToInt(List::size).max().getAsInt();
    }

    private static <T> T getLast(List<T> list) {
        return list.get(list.size()-1);
    }
}
