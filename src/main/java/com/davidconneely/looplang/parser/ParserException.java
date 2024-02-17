package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.LanguageException;

public final class ParserException extends LanguageException {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
