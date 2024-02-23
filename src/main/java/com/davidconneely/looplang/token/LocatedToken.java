package com.davidconneely.looplang.token;

import com.davidconneely.looplang.LocatedException;
import com.davidconneely.looplang.lexer.LexerException;
import com.davidconneely.looplang.lexer.Location;

/**
 * Always refers to at least one character (i.e. when leftCol == rightCol).
 * @param token underlying token we are wrapping with a location.
 * @param line one-based line number (zero indicates no information).
 * @param leftCol one-based column number (zero indicates no information).
 * @param rightCol one-based column number (zero indicates no information).
 */
public record LocatedToken(Token token, Location location) implements Token {
    @Override
    public Token.Kind kind() {
        return token.kind();
    }

    @Override
    public String value() {
        return token.value();
    }

    @Override
    public int valueInt() {
        return token.valueInt();
    }

    /**
     * Prevent re-location.
     */
    @Override
    public LocatedToken at(Location location) {
        throw new LexerException("attempt to relocate token to " + location, this);
    }

    @Override
    public String toString() {
        // TODO: for now don't include the location information in the toString() - does this make sense long-term?
        return token.toString();
    }
}
