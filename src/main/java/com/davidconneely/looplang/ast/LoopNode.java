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
    private String countVariableName;
    private List<Node> body;
    private Set<String> definedPrograms;

    LoopNode(Set<String> definedPrograms) {
        this.definedPrograms = definedPrograms;
    }

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KW_LOOP) {
            throw new ParserException("loop: expected `LOOP`; got " + token);
        }
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("loop: expected identifier (variable name); got " + token);
        }
        countVariableName = token.textValue();
        token = tokens.next();
        if (token.kind() != Token.Kind.KW_DO) {
            // `DO` is optional.
            tokens.pushback(token);
        }
        body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(tokens, Token.Kind.KW_END, definedPrograms);
        while (true) {
            Node node = parser.next();
            if (node != null) {
                body.add(node);
            } else {
                break;
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        final int icount;
        try {
            icount = context.getVariable(countVariableName);
        } catch (InterpreterException e) {
            throw new InterpreterException("loop: expected defined variable name; got " + countVariableName, e);
        }
        final Interpreter interpreter = InterpreterFactory.newInterpreter(context);
        for (int i = 0; i < icount; ++i) {
            for (Node node : body) {
                interpreter.interpret(node);
            }
        }
    }

    @Override
    public String toString() {
        if (countVariableName == null) {
            return "<uninitialized loop>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("LOOP ");
        sb.append(countVariableName.toLowerCase(Locale.ROOT));
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
