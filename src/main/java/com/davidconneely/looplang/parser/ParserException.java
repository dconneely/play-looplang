package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.LocatedException;
import com.davidconneely.looplang.token.Token;

public final class ParserException extends LocatedException {
    public ParserException(String message, Token token) {
        super(message, token);
    }
}
