package com.davidconneely.looplang.token;

import com.davidconneely.looplang.lexer.LexerException;

record SimpleToken(Kind kind, String value) implements Token {
    @Override
    public int valueInt() {
        if (kind() != Kind.NUMBER) {
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
            case NUMBER, IDENTIFIER, EOF, ASSIGN, PLUS, LPAREN, RPAREN, COMMA, SEMICOLON, KW_PROGRAM, KW_LOOP, KW_DO, KW_END, KW_INPUT, KW_PRINT ->
                    kind().name() + "[" + value() + "]";
        };
    }

    private static void throwNotNumericLexerException(String role, Token token) {
        throw new LexerException("not a numeric " + role + "; " + token, token);
    }
}
