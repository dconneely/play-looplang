package com.davidconneely.looplang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.token.Token;

final class DefinitionNode implements Node {
    private String program;
    private List<String> params;
    private List<Node> body;
    private final Set<String> programs;

    DefinitionNode(final Set<String> programs) {
        this.programs = programs;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() != Token.Kind.KW_PROGRAM) {
            throw new ParserException("definition: expected `PROGRAM`; got " + token);
        }
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("definition: expected identifier (program name); got " + token);
        }
        program = token.textValue();
        token = lexer.next();
        if (token.kind() != Token.Kind.LPAREN) {
            throw new ParserException("definition: expected `(`; got " + token);
        }
        params = new ArrayList<>();
        token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.RPAREN) {
            throw new ParserException("definition: expected identifier (param name) or `)`; got " + token);
        }
        while (token.kind() != Token.Kind.RPAREN) {
            params.add(token.textValue());
            token = lexer.next();
            if (token.kind() == Token.Kind.COMMA) {
                token = lexer.next();
                if (token.kind() != Token.Kind.IDENTIFIER) {
                    throw new ParserException("definition: expected identifier (param name); got " + token);
                }
            } else if (token.kind() != Token.Kind.RPAREN) {
                throw new ParserException("definition: expected ',' or ')'; got " + token);
            }
        }
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
        if (program == null || params == null || body == null) {
            throw new InterpreterException("uninitialized definition");
        }
        context.setProgram(program, params, body);
        programs.add(program);
    }

    @Override
    public String toString() {
        if (program == null || params == null || body == null) {
            return "<uninitialized definition>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("PROGRAM ");
        sb.append(program);
        sb.append('(');
        sb.append(params.stream().map(param -> param.toLowerCase(Locale.ROOT)).collect(Collectors.joining(", ")));
        sb.append(") DO\n");
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
