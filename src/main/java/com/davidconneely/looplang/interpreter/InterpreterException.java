package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.LanguageException;

public final class InterpreterException extends LanguageException {
    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }
}
