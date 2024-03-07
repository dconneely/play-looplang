package com.davidconneely.looplang.token;

import java.util.Map;

import static com.davidconneely.looplang.token.Token.Kind.*;

public final class TokenFactory {
    private static final Map<String, Token.Kind> keywords;

    static {
        keywords = Map.of("PROGRAM", PROGRAM, "LOOP", LOOP, "DO", DO, "END", END, "INPUT", INPUT, "PRINT", PRINT);
    }

    private TokenFactory() {
    }

    public static Token TOK_EOF = new SimpleToken(EOF, null);
    public static Token TOK_ASSIGN = new SimpleToken(ASSIGN, null);
    public static Token TOK_PLUS = new SimpleToken(PLUS, null);
    public static Token TOK_LPAREN = new SimpleToken(LPAREN, null);
    public static Token TOK_RPAREN = new SimpleToken(RPAREN, null);
    public static Token TOK_COMMA = new SimpleToken(COMMA, null);
    public static Token TOK_SEMICOLON = new SimpleToken(SEMICOLON, null);

    public static Token newString(final String string) {
        return new SimpleToken(STRING, string);
    }

    public static Token newNumber(final String number) {
        return new SimpleToken(NUMBER, number);
    }

    /**
     * Identifier or keyword (based on value).
     */
    public static Token newIdentifierOrKeyword(final String value) {
        final Token.Kind kind = keywords.getOrDefault(value, IDENTIFIER);
        return new SimpleToken(kind, kind == IDENTIFIER ? value : null);
    }
}
