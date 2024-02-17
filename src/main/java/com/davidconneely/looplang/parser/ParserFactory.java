package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.util.HashSet;
import java.util.Set;

public final class ParserFactory {
    private ParserFactory() {
    }

    public static Parser newParser(final Lexer tokens) {
        return new DefaultParser(tokens);
    }

    public static Parser newParser(final Lexer tokens, final Token.Kind until, final Set<String> definedPrograms) {
        return new DefaultParser(tokens, until, definedPrograms);
    }
}
