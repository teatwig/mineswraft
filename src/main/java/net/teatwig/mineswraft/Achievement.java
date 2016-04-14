package net.teatwig.mineswraft;

import lombok.AccessLevel;
import lombok.Getter;

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

    @Getter(AccessLevel.PACKAGE)
    private boolean obtained = false;

    static {
    }

    Achievement setObtainedAndGet() {
        this.obtained = true;
        return this;
    }

    void addToIfNotObtained(Set<Achievement> achievements) {
        if(this.obtained == false) {
            this.obtained = true;
            achievements.add(this);
        }
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
                stringBuilder.append(a.toString());
            }
        }
        return stringBuilder.toString();
    }

}
