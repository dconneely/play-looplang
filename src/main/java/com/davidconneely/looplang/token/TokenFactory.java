package com.davidconneely.looplang.token;

import java.util.HashMap;
import java.util.Map;

public final class TokenFactory {
    private static final Map<String, Token.Kind> keywords;

    static {
        keywords = new HashMap<>(5);
        keywords.put("PROGRAM", Token.Kind.KW_PROGRAM);
        keywords.put("LOOP", Token.Kind.KW_LOOP);
        keywords.put("DO", Token.Kind.KW_DO);
        keywords.put("END", Token.Kind.KW_END);
        keywords.put("INPUT", Token.Kind.KW_INPUT);
        keywords.put("PRINT", Token.Kind.KW_PRINT);
    }

    private TokenFactory() {
    }

    public static Token TOK_EOF = new DefaultToken(Token.Kind.EOF, "");
    public static Token TOK_NEWLINE = new DefaultToken(Token.Kind.NEWLINE, "\n");
    public static Token TOK_ASSIGN = new DefaultToken(Token.Kind.ASSIGN, ":=");
    public static Token TOK_PLUS = new DefaultToken(Token.Kind.PLUS, "+");
    public static Token TOK_LPAREN = new DefaultToken(Token.Kind.LPAREN, "(");
    public static Token TOK_RPAREN = new DefaultToken(Token.Kind.RPAREN, ")");
    public static Token TOK_COMMA = new DefaultToken(Token.Kind.COMMA, ",");
    public static Token TOK_SEMICOLON = new DefaultToken(Token.Kind.SEMICOLON, ";");

    public static Token newComment(final String comment) {
        return new DefaultToken(Token.Kind.COMMENT, comment);
    }

    public static Token newString(final String string) {
        return new DefaultToken(Token.Kind.STRING, string);
    }

    public static Token newNumber(final String number) {
        return new DefaultToken(Token.Kind.NUMBER, number);
    }

    /**
     * Identifier or keyword (based on value).
     */
    public static Token newIdentifierOrKeyword(final String value) {
        final Token.Kind kind = keywords.getOrDefault(value, Token.Kind.IDENTIFIER);
        return new DefaultToken(kind, value);
    }
}
