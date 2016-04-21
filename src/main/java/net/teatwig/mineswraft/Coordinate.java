package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by timo on 21.04.2016.
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Coordinate {
    @Getter(AccessLevel.PACKAGE)
    final int x, y;

    static Coordinate of(int x, int y) {
        return new Coordinate(x, y);
    }

    Stream<Coordinate> streamOfSurrounding() {
        Supplier<Coordinate> supplier = new Supplier<Coordinate>() {
            private int iter;
            @Override
            public Coordinate get() {
                switch (iter) {
                    case 0: iter++; return Coordinate.of(x-1, y-1);
                    case 1: iter++; return Coordinate.of(x, y-1);
                    case 2: iter++; return Coordinate.of(x+1, y-1);
                    case 3: iter++; return Coordinate.of(x-1, y);
                    case 4: iter++; return Coordinate.of(x+1, y);
                    case 5: iter++; return Coordinate.of(x-1, y+1);
                    case 6: iter++; return Coordinate.of(x, y+1);
                    case 7: iter++; return Coordinate.of(x+1, y+1);
                    default: System.err.println("ArrSupplier only generates 8 pairs"); return null;
                }
            }
        };

        Stream<Coordinate> stream = Stream.generate(() -> supplier.get()).limit(8);

        return stream;
    }
}
