package com.davidconneely.looplang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

final class PrintNode implements Node {
    private List<Token> args;
    private boolean appendNewline;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.KW_PRINT) {
            throw new ParserException("output: expected `PRINT`; got " + token);
        }
        args = new ArrayList<>();
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.STRING) {
            // no params
            lexer.pushback(token);
            appendNewline = true; // plain old `PRINT` with no params
            return;
        }
        while (true) {
            args.add(token);
            token = lexer.next();
            if (token.kind() == Token.Kind.COMMA) {
                token = lexer.next();
                if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.STRING) {
                    lexer.pushback(token);
                    appendNewline = false; // `PRINT <args>,` (i.e. trailing comma)
                    return;
                }
            } else {
                lexer.pushback(token);
                appendNewline = true; // `PRINT <args>` (i.e. no trailing comma)
                return;
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        StringBuilder sb = new StringBuilder();
        boolean lastString = true;
        for (Token token : args) {
            switch (token.kind()) {
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
                case STRING:
                    sb.append(token.textValue());
                    lastString = true;
                    break;
                default:
                    throw new InterpreterException("output: expected identifier (variable name) or string literal; got " + token);
            }
        }
        String str = sb.toString();
        if (appendNewline) {
            System.out.println(str);
        } else {
            System.out.print(str);
        }
    }

    @Override
    public String toString() {
        if (args == null) {
            return "<uninitialized output>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("PRINT ");
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
                    sb.append(Token.escaped(str));
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
}
