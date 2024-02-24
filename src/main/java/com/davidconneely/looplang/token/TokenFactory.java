package com.davidconneely.looplang.token;

import java.util.Map;

public final class TokenFactory {
    private static final Map<String, Token.Kind> keywords;

    static {
        keywords = Map.of("PROGRAM", Token.Kind.KW_PROGRAM, "LOOP", Token.Kind.KW_LOOP, "DO", Token.Kind.KW_DO, "END", Token.Kind.KW_END, "INPUT", Token.Kind.KW_INPUT, "PRINT", Token.Kind.KW_PRINT);
    }

    private TokenFactory() {
    }

    public static Token TOK_EOF = new SimpleToken(Token.Kind.EOF, "");
    public static Token TOK_ASSIGN = new SimpleToken(Token.Kind.ASSIGN, ":=");
    public static Token TOK_PLUS = new SimpleToken(Token.Kind.PLUS, "+");
    public static Token TOK_LPAREN = new SimpleToken(Token.Kind.LPAREN, "(");
    public static Token TOK_RPAREN = new SimpleToken(Token.Kind.RPAREN, ")");
    public static Token TOK_COMMA = new SimpleToken(Token.Kind.COMMA, ",");
    public static Token TOK_SEMICOLON = new SimpleToken(Token.Kind.SEMICOLON, ";");

    public static Token newString(final String string) {
        return new SimpleToken(Token.Kind.STRING, string);
    }

    public static Token newNumber(final String number) {
        return new SimpleToken(Token.Kind.NUMBER, number);
    }

    /**
     * Identifier or keyword (based on value).
     */
    public static Token newIdentifierOrKeyword(final String value) {
        final Token.Kind kind = keywords.getOrDefault(value, Token.Kind.IDENTIFIER);
        return new SimpleToken(kind, value);
    }
}
