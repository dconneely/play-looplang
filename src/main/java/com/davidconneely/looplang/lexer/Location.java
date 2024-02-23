package com.davidconneely.looplang.lexer;

/**
 * Not a record because we want this to be mutable.
 */
public final class Location {
    private final String filename; // create a new instance for each file.
    private int line;
    private int leftCol;
    private int rightCol;

    private Location(final String filename, final int line, final int leftCol, final int rightCol) {
        this.filename = filename;
        this.line = line;
        this.leftCol = leftCol;
        this.rightCol = rightCol;
    }

    public static Location newFile(final String filename) {
        return new Location(filename, 0, 0, 0);
    }

    public static Location copyOf(final Location other) {
        return new Location(other.filename, other.line, other.leftCol, other.rightCol);
    }

    // --- Mutators ---

    /** Start a new token. */
    public void nextToken() {
        ++rightCol;
        leftCol = rightCol;
    }

    /** Start a new line. */
    public void nextLine() {
        ++line;
        leftCol = rightCol = 0;
    }

    /**  Extend the current location by one character to the right. */
    public void extendToken() {
        ++rightCol;
    }

    // --- Getters ---

    public String filename() {
        return filename;
    }

    /** One-based line number (zero indicates no information). */
    public int line() {
        return line;
    }

    /** One-based column number (zero indicates no information). */
    public int leftCol() {
        return leftCol;
    }

    /** One-based column number (zero indicates no information). */
    public int rightCol() {
        return rightCol;
    }

    private String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(filename).append(':');
        if (line != 0) {
            sb.append(line).append(':');
            if (leftCol != 0) {
                sb.append(leftCol);
                if (leftCol != rightCol) {
                    sb.append('-').append(rightCol);
                }
                sb.append(':');
            }
        }
        sb.append(' ');
        return sb.toString();
    }

    @Override
    public String toString() {
        return format();
    }
}
