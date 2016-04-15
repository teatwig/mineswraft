package net.teatwig.mineswraft;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timo on 15.04.2016.
 */
class NonogramNumbers {
    private List<List<Integer>> xNumbers, yNumbers;

    NonogramNumbers(Board board) {
        xNumbers = new ArrayList<>();
        yNumbers = new ArrayList<>();

        setNumbersInFrom(xNumbers, flipDiagonally(board.getFields()));
        setNumbersInFrom(yNumbers, board.getFields());
    }

    private void setNumbersInFrom(List<List<Integer>> lists, Field[][] fields) {
        for(Field[] fArr : fields) {
            lists.add(new ArrayList<>());
            int currentNumber = 0;
            for(Field f : fArr) {
                if(f.isMine()) {
                    currentNumber++;
                } else if(!f.isMine()) {
                    if(currentNumber>0)
                        getLast(lists).add(currentNumber);
                    currentNumber = 0;
                }
            }
            if(currentNumber>0) {
                getLast(lists).add(currentNumber);
            }
        }
    }

    GridPane getXNumberAsGridPane() {
        GridPane gridPane = new GridPane();
        for(int i=0; i<xNumbers.size(); i++) {
            if(xNumbers.get(i).isEmpty()) {
                Button b = new Button();
                b.setVisible(false);
                gridPane.add(b, i, 0);
            }
            for(int j=0; j<xNumbers.get(i).size(); j++) {
                int deltaSize = getXNumMaxSize() - xNumbers.get(i).size();
                gridPane.add(new Button(xNumbers.get(i).get(j).toString()), i, j+deltaSize);
            }
        }
        applyButtonStyles(gridPane, true);
        return gridPane;
    }

    GridPane getYNumberAsGridPane() {
        GridPane gridPane = new GridPane();
        for(int i=0; i<yNumbers.size(); i++) {
            if(yNumbers.get(i).isEmpty()) {
                Button b = new Button();
                b.setVisible(false);
                gridPane.add(b, 0, i);
            }
            for(int j=0; j<yNumbers.get(i).size(); j++) {
                int deltaSize = getYNumMaxSize() - yNumbers.get(i).size();
                gridPane.add(new Button(yNumbers.get(i).get(j).toString()), j+deltaSize, i);
            }
        }
        applyButtonStyles(gridPane, false);
        return gridPane;
    }

    void applyButtonStyles(GridPane gridPane, boolean xNumbers) {
        gridPane.getChildren().stream().forEach(child -> {
            Button b = (Button) child;
            b.setPrefSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setMinSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setMaxSize(Controller.BUTTON_SIZE, Controller.BUTTON_SIZE);
            b.setStyle("-fx-border-style: "+(xNumbers?"hidden solid":"solid hidden")+
                    "; -fx-border-color: #DDD; -fx-border-size: 1px; -fx-background-radius: 0; -fx-background-color: #EFEFEF");
        });
    }

    private int getXNumMaxSize() {
        return getSize(xNumbers);
    }

    private int getYNumMaxSize() {
        return getSize(yNumbers);
    }

    private <T> int getSize(List<List<T>> lists) {
        return lists.stream().mapToInt(list -> list.size()).max().getAsInt();
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
