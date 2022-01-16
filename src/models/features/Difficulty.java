package models.features;

/**
 * Difficulty
 *
 * @author Alexander
 */

public class Difficulty implements Comparable<Difficulty> {
    private final int w, h, bombCount;
    private final String stringOverwrite;

    public static final Difficulty EASY = new Difficulty(9, 9, 10, "Easy");
    public static final Difficulty MEDIUM = new Difficulty(16, 16, 40, "Medium");
    public static final Difficulty HARD = new Difficulty(30, 16, 99, "Hard");
    public static final Difficulty CUSTOM = new Difficulty("Custom");

    public Difficulty (int w, int h, int bombCount, String string) {
        this.w = w;
        this.h = h;
        this.bombCount = bombCount;
        this.stringOverwrite = string;
    }

    public Difficulty (int w, int h, int bombCount) {
        this(w, h, bombCount, null);
    }

    public Difficulty (String string) {
        w = -1;
        h = -1;
        bombCount = -1;
        this.stringOverwrite = string;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getBombCount() {
        return bombCount;
    }

    public boolean equals (Object otherObject) {
        if (otherObject == null || otherObject.getClass() != Difficulty.class) return false;
        Difficulty other = (Difficulty) otherObject;
        return w == other.w && h == other.h && bombCount == other.bombCount;
    }

    public String toString () {
        if (stringOverwrite != null) return stringOverwrite;
        return w + "x" + h + ", " + bombCount + " bombs";
    }

    @Override
    public int compareTo(Difficulty o) {
        if (w == o.w) {
            if (h == o.h) {
                return bombCount - o.bombCount;
            }
            return h - o.h;
        }
        return w - o.w;
    }
}