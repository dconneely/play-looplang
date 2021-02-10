package uk.conneely.toylang.token;

import java.util.HashMap;
import java.util.Map;

public final class TokenFactory {
    private static final Map<String, Token.Kind> keywords;

    static {
        keywords = new HashMap<>(5);
        keywords.put("def", Token.Kind.KWDEF);
        keywords.put("for", Token.Kind.KWFOR);
        keywords.put("inc", Token.Kind.KWINC);
        keywords.put("println", Token.Kind.KWPRINTLN);
        keywords.put("stz", Token.Kind.KWSTZ);
    }

    private TokenFactory() {
    }

    public static Token eof() {
        return new DefaultToken(Token.Kind.EOF, "");
    }

    public static Token lnsep() {
        return new DefaultToken(Token.Kind.LNSEP, "\n");
    }

    public static Token cmmnt(final String comment) {
        return new DefaultToken(Token.Kind.CMMNT, comment);
    }

    public static Token stsep() {
        return new DefaultToken(Token.Kind.STSEP, ";");
    }

    public static Token ident(final String identifier) {
        return new DefaultToken(Token.Kind.IDENT, identifier);
    }

    public static Token intnum(final String number) {
        return new DefaultToken(Token.Kind.INTNUM, number);
    }

    public static Token strlit(final String string) {
        return new DefaultToken(Token.Kind.STRLIT, string);
    }

    public static Token oparen() {
        return new DefaultToken(Token.Kind.OPAREN, "(");
    }

    public static Token cparen() {
        return new DefaultToken(Token.Kind.CPAREN, ")");
    }

    /**
     * Keyword or identifier (based on passed value).
     */
    public static Token kwident(final String value) {
        final Token.Kind kind = keywords.getOrDefault(value, Token.Kind.IDENT);
        return new DefaultToken(kind, value);
    }
}
