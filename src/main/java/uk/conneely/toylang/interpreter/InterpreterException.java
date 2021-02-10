package uk.conneely.toylang.interpreter;

import uk.conneely.toylang.ToyException;

public final class InterpreterException extends ToyException {
    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }
}
