package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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

    Stream<Coordinate> getStreamOfSurrounding() {
        return Stream.of(
                Coordinate.of(x-1, y-1), Coordinate.of(x, y-1), Coordinate.of(x+1, y-1),
                Coordinate.of(x-1, y), Coordinate.of(x+1, y),
                Coordinate.of(x-1, y+1), Coordinate.of(x, y+1), Coordinate.of(x+1, y+1));
    }
}
