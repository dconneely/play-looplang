package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.LocatedException;
import com.davidconneely.looplang.token.Token;

public class LexerException extends LocatedException {
    public LexerException(final String message, final Location location) {
        super(message, location);
    }

    public LexerException(final String message, final Token token) {
        super(message, token);
    }
}
