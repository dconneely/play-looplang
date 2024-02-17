package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.LanguageException;

public class LexerException extends LanguageException {
    public LexerException(String message) {
        super(message);
    }

    public LexerException(String message, Throwable cause) {
        super(message, cause);
    }
}
