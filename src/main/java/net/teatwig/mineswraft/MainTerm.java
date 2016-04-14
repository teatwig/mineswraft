package net.teatwig.mineswraft;

import java.util.Scanner;

/**
 * Created by timo on 03.03.2016.
 */
public class MainTerm {
    public static void main(String... args) {
        Board board = new Board(10, 10, 10); // max mines = (x-1)(y-1)
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println(board);
            System.out.println("Show me your moves: (optional)mark X-Coordinate Y-Coordinate");
            String[] line = sc.nextLine().split(" ");
            boolean mark = false;
            int intStart = 0;
            if(line.length == 3) {
                if (line[0].equals("mark") || line[0].equals("m")){
                    mark = true;
                    intStart += 1;
                } else {
                    System.out.println("Invalid input.");
                    continue;
                }
            }

            try {
                int x_cord = Integer.parseInt(line[intStart]) - 1;
                int y_cord = Integer.parseInt(line[intStart+1]) - 1;
                if(mark)
                    board.toggleMarking(x_cord, y_cord);
                else
                    board.click(x_cord, y_cord);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                System.out.println("Invalid input.");
            }
        }
    }
}
