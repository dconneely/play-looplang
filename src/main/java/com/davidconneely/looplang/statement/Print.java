package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

record Print(List<Token> printTokens) implements Statement {
    static Print parse(final ParserContext context, final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, KW_PRINT, "in print");
        List<Token> printTokens = nextPrintTokens(lexer, "in print arguments");
        return new Print(printTokens);
    }

    static List<Token> nextPrintTokens(final Lexer lexer, final String role) throws IOException {
        List<Token> tokens = new ArrayList<>();
        nextTokenWithKind(lexer, LPAREN, role);  // parentheses in PRINT() and INPUT() are required now.
        Token token = lexer.next();
        while (isPrintTokenKind(token.kind())) {
            tokens.add(token);
            token = lexer.next();
            if (token.kind() != COMMA) {
                break;
            }
            token = lexer.next();
        }
        lexer.pushback(token);
        nextTokenWithKind(lexer, RPAREN, role);
        return tokens;
    }

    private static boolean isPrintTokenKind(final Token.Kind kind) {
        return kind == STRING || kind == NUMBER || kind == IDENTIFIER;
    }

    @Override
    public void interpret(final InterpreterContext context) {
        System.out.println(printTokensToText(printTokens, context));
    }

    static String printTokensToText(final List<Token> printTokens, final InterpreterContext context) {
        final StringBuilder sb = new StringBuilder();
        boolean wasLastTokenString = true;
        for (Token token : printTokens) {
            boolean isThisTokenString = (token.kind() == STRING);
            if (!wasLastTokenString && !isThisTokenString) {
                sb.append(' ');
            }
            sb.append(switch (token.kind()) {
                case NUMBER -> Integer.toString(token.valueInt());
                case IDENTIFIER ->
                        context.getVariable(token.value()).stream().mapToObj(Integer::toString).findFirst().orElse("undefined");
                default -> token.value();
            });
            wasLastTokenString = isThisTokenString;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PRINT(" + printTokensToString(printTokens) + ")";
    }

    static String printTokensToString(final List<Token> printTokens) {
        return printTokens.stream().map(token -> switch (token.kind()) {
            case STRING -> Token.escaped(token.value());
            case NUMBER -> Integer.toString(token.valueInt());
            case IDENTIFIER -> token.value().toLowerCase(Locale.ROOT);
            default -> token.value();
        }).collect(Collectors.joining(", "));
    }
}
