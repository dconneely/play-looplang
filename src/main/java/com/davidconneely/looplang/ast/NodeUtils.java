package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;

public final class NodeUtils {
    private NodeUtils() {
        // prevent instantiation.
    }

    public static Token nextTokenWithKind(final Lexer lexer, final Token.Kind expected, final String role) throws IOException {
        Token token = lexer.next();
        if (token.kind() != expected) {
            throwUnexpectedParserException(expected, role, token);
        }
        return token;
    }

    public static void throwUnexpectedParserException(Token.Kind expected, String role, Token actual) {
        throw new ParserException("expected " + expected + " " + role + "; got " + actual, actual);
    }

    public static void throwUnexpectedParserException(Token.Kind expected1, Token.Kind expected2, String role, Token actual) {
        throw new ParserException("expected " + expected1 + " or " + expected2 + " " + role + "; got " + actual, actual);
    }

    public static void throwUnexpectedParserException(Token.Kind expected1, Token.Kind expected2, Token.Kind expected3, String role, Token actual) {
        throw new ParserException("expected " + expected1 + " or " + expected2 + " or " + expected3 + " " + role + "; got " + actual, actual);
    }
}
