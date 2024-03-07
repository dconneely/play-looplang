package com.davidconneely.looplang.token;

import com.davidconneely.looplang.lexer.LexerException;

import static com.davidconneely.looplang.token.Token.Kind.NUMBER;

record SimpleToken(Kind kind, String value) implements Token {
    @Override
    public int valueInt() {
        if (kind() != NUMBER) {
            throwNotNumericLexerException("kind", this);
        }
        int number = -1;
        try {
            number = Integer.parseInt(value());
        } catch (NumberFormatException e) {
            throwNotNumericLexerException("value", this);
        }
        return number;
    }

    @Override
    public String toString() {
        return switch (kind()) {
            case STRING -> kind().name() + "[" + Token.escaped(value()) + "]";
            case NUMBER, IDENTIFIER -> kind().name() + "[" + value() + "]";
            case EOF, ASSIGN, PLUS, LPAREN, RPAREN, COMMA, SEMICOLON, PROGRAM, LOOP, DO, END, INPUT, PRINT ->
                    kind().name() + "[]";
        };
    }

    private static void throwNotNumericLexerException(final String role, final Token token) {
        throw new LexerException("not a numeric " + role + "; " + token, token);
    }
}
