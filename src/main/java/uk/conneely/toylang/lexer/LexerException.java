package uk.conneely.toylang.lexer;

import uk.conneely.toylang.ToyException;

public class LexerException extends ToyException {
    public LexerException(String message) {
        super(message);
    }

    public LexerException(String message, Throwable cause) {
        super(message, cause);
    }
}
