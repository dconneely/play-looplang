package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;

final class AssignInputNode implements Node {
    private String variable;
    private List<Token> printTokens;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        variable = nextTokenWithKind(lexer, Token.Kind.IDENTIFIER, "as lvalue variable name in input").textValue();
        nextTokenWithKind(lexer, Token.Kind.ASSIGN, "after lvalue in input");
        nextTokenWithKind(lexer, Token.Kind.KW_INPUT, "in input").textValue();
        printTokens = nextPrintTokens(lexer);
    }


    private List<Token> nextPrintTokens(final Lexer lexer) throws IOException {
        List<Token> tokens = new ArrayList<>();
        Token token = lexer.next();
        while (isPrintTokenKind(token.kind())) {
            tokens.add(token);
            token = lexer.next();
            if (token.kind() != Token.Kind.COMMA) {
                break;
            }
            token = lexer.next();
        }
        lexer.pushback(token);
        return tokens;
    }

    private static boolean isPrintTokenKind(Token.Kind kind) {
        return kind == Token.Kind.STRING || kind == Token.Kind.NUMBER || kind == Token.Kind.IDENTIFIER;
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || printTokens == null) {
            throw new InterpreterException("uninitialized input");
        }
        StringBuilder sb = new StringBuilder();
        boolean lastString = true;
        for (Token token : printTokens) {
            switch (token.kind()) {
                case STRING:
                    sb.append(token.textValue());
                    lastString = true;
                    break;
                case NUMBER:
                    if (!lastString) {
                        sb.append(' ');
                    }
                    sb.append(token.intValue());
                    lastString = false;
                case IDENTIFIER:
                    if (!lastString) {
                        sb.append(' ');
                    }
                    try {
                        sb.append(context.getVariable(token.textValue()));
                    } catch (InterpreterException e) {
                        sb.append("undefined");
                    }
                    lastString = false;
                    break;
                default:
                    sb.append(token.textValue());
                    lastString = false;
                    break;
            }
        }
        String str = sb.toString();
        System.out.print(str);
        context.setVariable(variable, Math.max(0, new Scanner(System.in).nextInt()));
    }

    @Override
    public String toString() {
        if (variable == null || printTokens == null) {
            return "<uninitialized input>";
        }
        return variable + " := INPUT " + printTokens.stream().map(token -> switch (token.kind()) {
            case STRING -> Token.escaped(token.textValue());
            case NUMBER -> Integer.toString(token.intValue());
            case IDENTIFIER -> token.textValue().toLowerCase(Locale.ROOT);
            default -> token.textValue();
        }).collect(Collectors.joining(", "));
    }
}
