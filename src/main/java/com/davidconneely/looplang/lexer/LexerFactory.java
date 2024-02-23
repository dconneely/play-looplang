package com.davidconneely.looplang.lexer;

import java.io.Reader;

public final class LexerFactory {
    private LexerFactory() {
    }

    public static Lexer newLexer(final Location location, final CharInput input) {
        return new DefaultLexer(location, input);
    }

    public static Lexer newLexer(final Location location, final Reader reader) {
        return newLexer(location, new ReaderCharInput(reader));
    }
}
