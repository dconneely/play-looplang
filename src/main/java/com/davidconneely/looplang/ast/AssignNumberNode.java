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

final class AssignNumberNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private int number = -1;

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assignnumber: expected identifier (variable name); got " + token);
        }
        variable = token.textValue();
        token = lexer.next();
        if (token.kind() != Token.Kind.ASSIGN) {
            throw new ParserException("assignnumber: expected `:=`; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.NUMBER) {
            throw new ParserException("assignnumber: expected number; got " + token);
        }
        number = token.intValue();
        if (number != 0) {
            throw new ParserException("assignnumber: expected `0` as number; got " + token);
        }
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || number < 0) {
            throw new InterpreterException("uninitialized assignnumber");
        }
        context.setVariable(variable, number);
    }

    @Override
    public String toString() {
        if (variable == null || number < 0) {
            return "<uninitialized assignnumber>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(variable.toLowerCase(Locale.ROOT));
        sb.append(" := ");
        sb.append(number);
        return sb.toString();
    }
}
