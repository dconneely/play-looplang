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
import static com.davidconneely.looplang.token.Token.Kind.*;

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
        nextTokenWithKind(lexer, KW_PROGRAM, "in definition");
        program = nextTokenWithKind(lexer, IDENTIFIER, "as program in definition").textValue();
        nextTokenWithKind(lexer, LPAREN, "in definition");
        params = nextTokensAsCSVNames(lexer, "in params list in definition");
        Token token = lexer.next();
        if (token.kind() != KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        body = parseBody(lexer, programs);
    }

    static List<String> nextTokensAsCSVNames(final Lexer lexer, final String role) throws IOException {
        List<String> names = new ArrayList<>();
        Token token = lexer.next();
        if (token.kind() != IDENTIFIER && token.kind() != RPAREN) {
            throwUnexpectedParserException(IDENTIFIER, RPAREN, role, token);
        }
        while (token.kind() != RPAREN) {
            names.add(token.textValue());
            token = nextTokensAsCSVToken(lexer, role);
        }
        return names;
    }

    private static Token nextTokensAsCSVToken(final Lexer lexer, final String role) throws IOException {
        Token token = lexer.next();
        if (token.kind() == COMMA) {
            token = lexer.next();
            if (token.kind() != IDENTIFIER) {
                throwUnexpectedParserException(IDENTIFIER, role, token);
            }
        } else if (token.kind() != RPAREN) {
            throwUnexpectedParserException(COMMA, RPAREN, role, token);
        }
        return token;
    }

    static List<Node> parseBody(final Lexer lexer, Set<String> programs) throws IOException {
        List<Node> body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(lexer, KW_END, programs);
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
