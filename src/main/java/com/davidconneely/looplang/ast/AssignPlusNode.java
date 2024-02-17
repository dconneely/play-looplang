package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

final class AssignPlusNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private int number = -1;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assignplus: expected identifier (variable name); got " + token);
        }
        variable = token.textValue();
        token = lexer.next();
        if (token.kind() != Token.Kind.ASSIGN) {
            throw new ParserException("assignplus: expected `:=`; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assignplus: expected identifier (variable name); got " + token);
        }
        String variable2 = token.textValue();
        if (!variable.equals(variable2)) {
            throw new ParserException("assignplus: expected identifier (variable name) in lvalue (`" + variable.toLowerCase(Locale.ROOT) + "`) and rvalue (`" + variable2.toLowerCase(Locale.ROOT) + "`) to match");
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.PLUS) {
            throw new ParserException("assignplus: expected '+' ; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.NUMBER) {
            throw new ParserException("assignplus: expected number; got " + token);
        }
        number = token.intValue();
        if (number != 1) {
            throw new ParserException("assignplus: expected `1` as number; got " + token);
        }
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || number < 0) {
            throw new InterpreterException("uninitialized assignplus");
        }
        context.setVariable(variable, context.getVariable(variable) + number);
    }

    @Override
    public String toString() {
        if (variable == null || number < 0) {
            return "<uninitialized assignplus>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(variable.toLowerCase(Locale.ROOT));
        sb.append(" := ");
        sb.append(variable.toLowerCase(Locale.ROOT));
        sb.append(" + ");
        sb.append(number);
        return sb.toString();
    }
}
