package com.davidconneely.looplang.lexer;

import java.io.Reader;

public final class LexerFactory {
    private LexerFactory() {
    }

    public static Lexer lexer(final CharInput input) {
        return new DefaultLexer(input);
    }

    public static Lexer lexer(final Reader reader) {
        return new DefaultLexer(new ReaderCharInput(reader));
    }

}
