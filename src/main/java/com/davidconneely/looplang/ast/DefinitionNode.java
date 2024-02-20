package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.ast.NodeUtils.throwUnexpectedParserException;

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
        nextTokenWithKind(lexer, Token.Kind.KW_PROGRAM, "in definition");
        program = nextTokenWithKind(lexer, Token.Kind.IDENTIFIER, "as program in definition").textValue();
        nextTokenWithKind(lexer, Token.Kind.LPAREN, "in definition");
        params = nextTokensAsParams(lexer);
        Token token = lexer.next();
        if (token.kind() != Token.Kind.KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        body = parseBody(lexer);
    }

    private static List<String> nextTokensAsParams(final Lexer lexer) throws IOException {
        List<String> params = new ArrayList<>();
        Token token = lexer.next();
        if (token.kind() != Token.Kind.IDENTIFIER && token.kind() != Token.Kind.RPAREN) {
            throwUnexpectedParserException(Token.Kind.IDENTIFIER, Token.Kind.RPAREN, "in params list in definition", token);
        }
        while (token.kind() != Token.Kind.RPAREN) {
            params.add(token.textValue());
            token = nextTokensCommaSepParam(lexer);
        }
        return params;
    }

    private static Token nextTokensCommaSepParam(final Lexer lexer) throws IOException {
        Token token = lexer.next();
        if (token.kind() == Token.Kind.COMMA) {
            token = lexer.next();
            if (token.kind() != Token.Kind.IDENTIFIER) {
                throwUnexpectedParserException(Token.Kind.IDENTIFIER, "as param in definition", token);
            }
        } else if (token.kind() != Token.Kind.RPAREN) {
            throwUnexpectedParserException(Token.Kind.COMMA, Token.Kind.RPAREN, "after param in definition", token);
        }
        return token;
    }

    private List<Node> parseBody(final Lexer lexer) throws IOException {
        List<Node> body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(lexer, Token.Kind.KW_END, programs);
        Node node = parser.next();
        while (node != null) {
            body.add(node);
            node = parser.next();
        }
        return body;
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
        final List<String> lines = new ArrayList<>();
        lines.add("PROGRAM " + program + '(' + params.stream().map(param -> param.toLowerCase(Locale.ROOT)).collect(Collectors.joining(", ")) + ") DO");
        body.forEach(node -> lines.add(node.toString().indent(2).stripTrailing()));
        lines.add("END");
        return String.join("\n", lines);
    }
}
