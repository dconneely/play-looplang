package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class InputNode implements Node {
    private List<Token> args;
    private boolean appendNewline;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KW_INPUT) {
            throw new ParserException("input: expected `INPUT`; got " + token);
        }
        args = new ArrayList<>();
        int countVariables = 0;
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.STRING) {
            tokens.pushback(token);
            // we don't allow have plain old `INPUT` with no params
            throw new ParserException("input: expected identifier (variable name) or string; got " + token);
        }
        while (true) {
            if (token.kind() == Token.Kind.IDENTIFIER) {
                ++countVariables;
            }
            args.add(token);
            token = tokens.next();
            if (token.kind() == Token.Kind.COMMA) {
                token = tokens.next();
                if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.STRING) {
                    tokens.pushback(token);
                    appendNewline = false; // `INPUT <args>,` (i.e. trailing comma)
                    if (countVariables < 1) {
                        throw new ParserException("input: expected at least one identifier (variable name); got none");
                    }
                    return;
                }
            } else {
                tokens.pushback(token);
                appendNewline = true; // `INPUT <args>` (i.e. no trailing comma)
                if (countVariables < 1) {
                    throw new ParserException("input: expected at least one identifier (variable name); got none");
                }
                return;
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        boolean lastString = false;
        for (Token token : args) {
            switch (token.kind()) {
                case IDENTIFIER:
                    if (!lastString) {
                        System.out.print("?");
                    }
                    // don't allow negative values...
                    context.setVariable(token.textValue(), Math.max(0, new Scanner(System.in).nextInt()));
                    lastString = false;
                    break;
                case STRING:
                    System.out.print(token.textValue());
                    lastString = true;
                    break;
                default:
                    throw new InterpreterException("input: expected identifier (variable name) or string literal; got " + token);
            }
        }
        if (appendNewline && lastString) {
            System.out.println();
        }
    }

    @Override
    public String toString() {
        if (args == null) {
            return "<uninitialized input>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("INPUT ");
        boolean first = true;
        for (Token token : args) {
            if (!first) { sb.append(", "); } else { first = false; }
            switch (token.kind()) {
                case IDENTIFIER:
                    String variableName = token.textValue();
                    sb.append(variableName.toLowerCase(Locale.ROOT));
                    break;
                case STRING:
                    String str = token.textValue();
                    sb.append('"');
                    sb.append(escaped(str));
                    sb.append('"');
                    break;
                default:
                    sb.append("<unexpected ");
                    sb.append(token);
                    sb.append(">");
                    break;
            }
        }
        if (!appendNewline) {
            sb.append(',');
        }
        return sb.toString();
    }

    private static String escaped(String val) {
        return val.replace("\r\n", "\n")
                .replace("\n", "\\n")
                .replace("\"", "\\\"");
    }


}