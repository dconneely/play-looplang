package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class PrintNode implements Node {
    private List<Token> printTokens;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, KW_PRINT, "in print");
        printTokens = nextPrintTokens(lexer, "in print arguments");
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

    private static boolean isPrintTokenKind(Token.Kind kind) {
        return kind == STRING || kind == NUMBER || kind == IDENTIFIER;
    }

    @Override
    public void interpret(final InterpreterContext context) {
        if (printTokens == null) {
            throw new InterpreterException("uninitialized print");
        }
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
        if (printTokens == null) {
            return "<uninitialized print>";
        }
        return "PRINT(" + printTokensToString(printTokens) + ")";
    }

    static String printTokensToString(List<Token> printTokens) {
        return printTokens.stream().map(token -> switch (token.kind()) {
            case STRING -> Token.escaped(token.value());
            case NUMBER -> Integer.toString(token.valueInt());
            case IDENTIFIER -> token.value().toLowerCase(Locale.ROOT);
            default -> token.value();
        }).collect(Collectors.joining(", "));
    }
}
