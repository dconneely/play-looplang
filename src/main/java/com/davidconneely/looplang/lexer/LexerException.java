package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.LocatedException;
import com.davidconneely.looplang.token.Token;

public class LexerException extends LocatedException {
    public LexerException(String message, Location location) {
        super(message, location);
    }

    public LexerException(String message, Token token) {
        super(message, token);
    }
}
