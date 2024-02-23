package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class DefinitionNode implements Node {
    private final ParserContext context;
    private String program;
    private List<String> params;
    private List<Node> body;

    DefinitionNode(final ParserContext context) {
        this.context = context;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, KW_PROGRAM, "in definition");
        program = nextTokenWithKind(lexer, IDENTIFIER, "as program in definition").value();
        params = nextTokensAsCSVNames(lexer, "in params list in definition");
        Token token = lexer.next();
        if (token.kind() != KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        body = parseBody(lexer, context);
    }

    static List<String> nextTokensAsCSVNames(final Lexer lexer, final String role) throws IOException {
        List<String> names = new ArrayList<>();
        nextTokenWithKind(lexer, LPAREN, role);  // parentheses in PROGRAM definition and call are required.
        Token token = lexer.next();
        while (token.kind() == IDENTIFIER) {
            names.add(token.value());
            token = lexer.next();
            if (token.kind() != COMMA) {
                break;
            }
            token = lexer.next();
        }
        lexer.pushback(token);
        nextTokenWithKind(lexer, RPAREN, role);
        return names;
    }

    static List<Node> parseBody(final Lexer lexer, final ParserContext context) throws IOException {
        List<Node> body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(lexer, context, KW_END);
        Node node = parser.next();
        while (node != null) {
            body.add(node);
            node = parser.next();
        }
        return body;
    }

    @Override
    public void interpret(final InterpreterContext interpreterContext) {
        if (program == null || params == null || body == null) {
            throw new InterpreterException("uninitialized definition");
        }
        interpreterContext.setProgram(program, params, body);
        // now update the ParserContext as the act of defining a program changes parser behavior!
        // TODO: can this be done in a less hacky way?
        context.addDefinedProgram(program);
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
