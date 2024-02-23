package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.LocatedException;

public final class InterpreterException extends LocatedException {
    public InterpreterException(final String message) {
        super(message);
    }

    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }
}
