package net.teatwig.mineswraft;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by timo on 01.04.2016.
 */
enum Achievement {
    EASY_WIN,   EASY_NOMARK,
    MEDIUM_WIN, MEDIUM_NOMARK,
    HARD_WIN,   HARD_NOMARK,
    EXP_ACHIEVE;

    private boolean obtained = false;

    static {
    }

    boolean isNotObtained() {
        return !obtained;
    }

    boolean isObtained() {
        return obtained;
    }

    void setObtained() {
        this.obtained = true;
    }

    Achievement setObtainedAndGet() {
        setObtained();
        return this;
    }

    static Set<Achievement> getSet() {
        return EnumSet.allOf(Achievement.class);
    }

    static String allToString() {
        StringBuilder stringBuilder = new StringBuilder("\n\nObtained Achievements: ");

        boolean firstAdded = false;
        for(Achievement a : getSet()) {
            if(a.isObtained()) {
                if(firstAdded) {
                    stringBuilder.append(", ");
                } else {
                    firstAdded = true;
                }
                stringBuilder.append(a.name());
            }
        }
        return stringBuilder.toString();
    }

}
