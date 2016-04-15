package net.teatwig.mineswraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timo on 15.04.2016.
 */
public class NonogramNumbers {
    private List<List<Integer>> xNumbers, yNumbers;

    NonogramNumbers(Board board) {
        xNumbers = new ArrayList<>();
        yNumbers = new ArrayList<>();

        System.out.println(board.xRayBoardToString());

        getYNumbers(board.getFields());
        getXNumbers(board.getFields());
    }

    private void getXNumbers(Field[][] fields) {
        Field[][] flipFields = new Field[fields[0].length][fields.length];
        for(int i=0; i<fields.length; i++) {
            for(int j=0; j<fields[0].length; j++) {
                flipFields[j][i] = fields[i][j];
            }
        }

        setNumbersInFrom(xNumbers, flipFields);

        xNumbers.stream().forEach(System.out::println);
    }

    private void getYNumbers(Field[][] fields) {
        setNumbersInFrom(yNumbers, fields);

        yNumbers.stream().forEach(System.out::println);
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

    public int getXSize() {
        return getSize(xNumbers);
    }

    public int getYSize() {
        return getSize(yNumbers);
    }

    private <T> int getSize(List<List<T>> lists) {
        return lists.stream().mapToInt(list -> list.size()).max().getAsInt();
    }

    private static <T> T getLast(List<T> list) {
        return list.get(list.size()-1);
    }
}
