package com.davidconneely.looplang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.token.Token;

final class LoopNode implements Node {
    private String variable;
    private List<Node> body;
    private final Set<String> programs;

    LoopNode(final Set<String> programs) {
        this.programs = programs;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.KW_LOOP) {
            throw new ParserException("loop: expected `LOOP`; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("loop: expected identifier (variable name); got " + token);
        }
        variable = token.textValue();
        token = lexer.next();
        if (token.kind() != Token.Kind.KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(lexer, Token.Kind.KW_END, programs);
        Node node = parser.next();
        while (node != null) {
            body.add(node);
            node = parser.next();
        }
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || body == null) {
            throw new InterpreterException("uninitialized loop");
        }
        final int count;
        try {
            count = context.getVariable(variable);
        } catch (InterpreterException e) {
            throw new InterpreterException("loop: expected defined variable name; got " + variable, e);
        }
        final Interpreter interpreter = InterpreterFactory.newInterpreter(context);
        for (int i = 0; i < count; ++i) {
            for (Node node : body) {
                interpreter.interpret(node);
            }
        }
    }

    @Override
    public String toString() {
        if (variable == null || body == null) {
            return "<uninitialized loop>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("LOOP ");
        sb.append(variable.toLowerCase(Locale.ROOT));
        sb.append(" DO\n");
        boolean first = true;
        for (Node node : body) {
            if (first) { first = false; } else { sb.append(";\n"); }
            sb.append(node.toString().indent(2).stripTrailing());
        }
        if (!first) { sb.append('\n'); }
        sb.append("END");
        return sb.toString();
    }
}
