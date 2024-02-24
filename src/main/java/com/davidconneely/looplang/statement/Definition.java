package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.interpreter.InterpreterContext;
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

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

record Definition(String program, List<String> params, List<Statement> body) implements Statement {
    static Definition parse(final ParserContext context, final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, KW_PROGRAM, "in definition");
        final String program = nextTokenWithKind(lexer, IDENTIFIER, "as program in definition").value();
        final List<String> params = nextTokensAsCSVNames(lexer, "in params list in definition");
        Token token = lexer.next();
        if (token.kind() != KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        final List<Statement> body = parseBody(lexer, context);
        return new Definition(program, params, body);
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

    static List<Statement> parseBody(final Lexer lexer, final ParserContext context) throws IOException {
        List<Statement> body = new ArrayList<>();
        final Parser parser = ParserFactory.newParser(lexer, context, KW_END);
        Statement statement = parser.next();
        while (statement != null) {
            body.add(statement);
            statement = parser.next();
        }
        return body;
    }

    @Override
    public void interpret(final InterpreterContext interpreterContext) {
        interpreterContext.setProgram(program, params, body);
    }

    @Override
    public String toString() {
        final List<String> lines = new ArrayList<>();
        lines.add("PROGRAM " + program + '(' + params.stream().map(param -> param.toLowerCase(Locale.ROOT)).collect(Collectors.joining(", ")) + ") DO");
        body.forEach(node -> lines.add(node.toString().indent(2).stripTrailing()));
        lines.add("END");
        return String.join("\n", lines);
    }
}
