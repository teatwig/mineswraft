package net.teatwig.mineswraft;

import java.util.Scanner;

/**
 * Created by timo on 03.03.2016.
 */
public class MainTerm {
    public static void main(String... args) {
        Scanner sc = new Scanner(System.in);
        do {
            newGame(sc);
            System.out.println("Write \"n\" to start a new game or \"q\" to quit.");
            while(true) {
                String input = sc.nextLine();
                if(input.equals("n"))
                    break;
                if(input.equals("q"))
                    System.exit(0);
                else
                    System.out.println("Invalid input.");
            }
        } while (true);
    }

    private static void newGame(Scanner sc) {
        System.out.println("New game started.");
        Board board = new Board(10, 10, 10); // max mines = (x-1)(y-1)
        while (true) {
            System.out.println(board);
            System.out.println("Show me your moves: (optional)mark X-Coordinate Y-Coordinate");
            String[] line = sc.nextLine().split(" ");
            boolean mark = false;
            int intStart = 0;
            if (line.length == 3) {
                if (line[0].equals("mark") || line[0].equals("m")) {
                    mark = true;
                    intStart += 1;
                } else {
                    System.out.println("Invalid input.");
                    continue;
                }
            }

            try {
                Coordinate coordinate = Coordinate.of(Integer.parseInt(line[intStart]) - 1, Integer.parseInt(line[intStart + 1]) - 1);
                if (mark)
                    board.toggleMarking(coordinate);
                else
                    board.click(coordinate);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                System.out.println("Invalid input.");
            }

            if (board.isGameOver()) {
                System.out.println("Game over.");
                break;
            }
            if (board.isGameWon()) {
                System.out.println("Game won!");
                break;
            }
        }
    }
}
