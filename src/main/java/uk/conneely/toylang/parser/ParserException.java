package uk.conneely.toylang.parser;

import uk.conneely.toylang.ToyException;

public final class ParserException extends ToyException {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
