package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.LocatedException;
import com.davidconneely.looplang.token.Token;

public class ParserException extends LocatedException {
    public ParserException(final String message, final Token token) {
        super(message, token);
    }
}
