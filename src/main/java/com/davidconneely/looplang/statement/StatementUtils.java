package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;

public final class StatementUtils {
    private StatementUtils() {
        // prevent instantiation.
    }

    public static Token nextTokenWithKind(final Lexer lexer, final Token.Kind expected, final String role) throws IOException {
        Token token = lexer.next();
        if (token.kind() != expected) {
            throwUnexpectedParserException(expected, role, token);
        }
        return token;
    }

    public static void throwUnexpectedParserException(final Token.Kind expected, final String role, final Token actual) {
        throw new ParserException("expected " + expected + " " + role + "; got " + actual, actual);
    }

    public static void throwUnexpectedParserException(final Token.Kind expected1, final Token.Kind expected2, final String role, final Token actual) {
        throw new ParserException("expected " + expected1 + " or " + expected2 + " " + role + "; got " + actual, actual);
    }

    public static void throwUnexpectedParserException(final Token.Kind expected1, final Token.Kind expected2, final Token.Kind expected3, final String role, final Token actual) {
        throw new ParserException("expected " + expected1 + " or " + expected2 + " or " + expected3 + " " + role + "; got " + actual, actual);
    }
}
