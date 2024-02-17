package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.util.HashSet;
import java.util.Set;

public final class ParserFactory {
    private ParserFactory() {
    }

    public static Parser newParser(final Lexer lexer) {
        return new DefaultParser(lexer);
    }

    public static Parser newParser(final Lexer lexer, final Token.Kind until, final Set<String> programs) {
        return new DefaultParser(lexer, until, programs);
    }
}
