package com.davidconneely.looplang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.davidconneely.looplang.interpreter.GlobalContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.token.Token;

final class ProgramDefnNode implements Node {
    private String name;
    private List<String> params;
    private List<Node> body;
    private Set<String> definedPrograms;

    ProgramDefnNode(Set<String> definedPrograms) {
        this.definedPrograms = definedPrograms;
    }

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KW_PROGRAM) {
            throw new ParserException("programDefn: expected `PROGRAM`; got " + token);
        }
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENTIFIER) {
            throw new ParserException("programDefn: expected identifier (program name); got " + token);
        }
        name = token.textValue();
        token = tokens.next();
        if (token.kind() != Token.Kind.LPAREN) {
            throw new ParserException("programDefn: expected `(`; got " + token);
        }
        params = new ArrayList<>();
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.RPAREN) {
            throw new ParserException("programDefn: expected identifier (param name) or `)`; got " + token);
        }
        while (token.kind() != Token.Kind.RPAREN) {
            params.add(token.textValue());
            token = tokens.next();
            if (token.kind() == Token.Kind.COMMA) {
                token = tokens.next();
                if (token.kind() != Token.Kind.IDENTIFIER) {
                    throw new ParserException("programDefn: expected identifier (param name); got " + token);
                }
            } else if (token.kind() != Token.Kind.RPAREN) {
                throw new ParserException("programDefn: expected ',' or ')'; got " + token);
            }
        }
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
        context.defineProgram(name, params, body);
        definedPrograms.add(name);
    }

    @Override
    public String toString() {
        if (name == null || params == null || body == null) {
            return "<uninitialized programDefn>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("PROGRAM ");
        sb.append(name);
        sb.append('(');
        boolean first = true;
        for (String param : params) {
            if (first) { first = false; } else { sb.append(", "); }
            sb.append(param.toLowerCase(Locale.ROOT));
        }
        sb.append(") DO\n");
        first = true;
        for (Node node : body) {
            if (first) { first = false; } else { sb.append(";\n"); }
            sb.append(node.toString().indent(2).stripTrailing());
        }
        if (!first) { sb.append('\n'); }
        sb.append("END");
        return sb.toString();
    }
}
