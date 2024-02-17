package com.davidconneely.looplang.ast;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

final class AssignCallNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private String program;
    private List<String> args;
    private final Set<String> programs;

    AssignCallNode(final Set<String> programs) {
        this.programs = programs;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assigncall: expected identifier (variable name); got " + token);
        }
        variable = token.textValue();
        token = lexer.next();
        if (token.kind() != Token.Kind.ASSIGN) {
            throw new ParserException("assigncall: expected `:=`; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("assigncall: expected identifier (program name); got " + token);
        }
        program = token.textValue();
        if (!programs.contains(program)) {
            throw new ParserException("assigncall: disallowed call to program `" + program + "`, that has not been fully-defined"); // prevent recursive calls
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.LPAREN) {
            throw new ParserException("assigncall: expected `(`; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.RPAREN) {
            throw new ParserException("assigncall: expected identifier (variable name) or ')' in args; got " + token);
        }
        args = new ArrayList<>();
        while (token.kind() != Token.Kind.RPAREN) {
            args.add(token.textValue());
            token = lexer.next();
            if (token.kind() == Token.Kind.COMMA) {
                token = lexer.next();
                if (token.kind() != Token.Kind.IDENTIFIER) {
                    throw new ParserException("assigncall: expected identifier (variable name) in program call args; got " + token);
                }
            } else if (token.kind() != Token.Kind.RPAREN) {
                throw new ParserException("assigncall: expected ',' or ')' in args; got " + token);
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || program == null || args == null) {
            throw new InterpreterException("uninitialized assigncall");
        }
        Context subcontext = context.getProgramContext(program, args);
        try {
            subcontext.getVariable("X0");
        } catch (InterpreterException e) {
            subcontext.setVariable("X0", 0);
        }
        final List<Node> body = context.getProgramBody(program);
        final Interpreter interpreter = InterpreterFactory.newInterpreter(subcontext);
        for (Node node : body) {
            interpreter.interpret(node);
        }
        final int x0 = subcontext.getVariable("X0");
        context.setVariable(variable, x0);
    }

    @Override
    public String toString() {
        if (variable == null || program == null || args == null) {
            return "<uninitialized assigncall>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(variable.toLowerCase(Locale.ROOT));
        sb.append(" := ");
        sb.append(program.toUpperCase(Locale.ROOT));
        sb.append('(');
        sb.append(args.stream().map(arg -> arg.toLowerCase(Locale.ROOT)).collect(Collectors.joining(", ")));
        sb.append(')');
        return sb.toString();
    }
}
