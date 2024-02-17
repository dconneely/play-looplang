package com.davidconneely.looplang.lexer;

import java.io.Reader;

public final class LexerFactory {
    private LexerFactory() {
    }

    public static Lexer newLexer(final CharInput input) {
        return new DefaultLexer(input);
    }

    public static Lexer newLexer(final Reader reader) {
        return newLexer(new ReaderCharInput(reader));
    }
}
