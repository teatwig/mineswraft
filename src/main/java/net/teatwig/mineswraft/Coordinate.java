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
class Coordinate {

    @Getter(AccessLevel.PACKAGE)
    final int x, y;

    static Coordinate of(int x, int y) {
        return new Coordinate(x, y);
    }

    Stream<Coordinate> getStreamOfSurrounding() {
        return Stream.of(
                Coordinate.of(x - 1, y - 1), Coordinate.of(x, y - 1), Coordinate.of(x + 1, y - 1),
                Coordinate.of(x - 1, y), Coordinate.of(x + 1, y),
                Coordinate.of(x - 1, y + 1), Coordinate.of(x, y + 1), Coordinate.of(x + 1, y + 1)
        );
    }

    boolean isNotSurroundedBy(Coordinate other) {
        return other.x < this.x - 1 || other.x > this.x + 1 || other.y < this.y - 1 || other.y > this.y + 1;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Coordinate) {
            Coordinate coordinate = (Coordinate) other;
            return this.x == coordinate.x && this.y == coordinate.y;
        } else {
            return false;
        }
    }

}
