package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

record Loop(String variable, List<Statement> body) implements Statement {
    static Loop parse(final ParserContext context, final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, LOOP, "in loop");
        final String variable = nextTokenWithKind(lexer, IDENTIFIER, "as count variable in loop").value();
        Token token = lexer.next();
        if (token.kind() != DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        final List<Statement> body = Definition.parseBody(lexer, context);
        return new Loop(variable, body);
    }

    @Override
    public void interpret(final InterpreterContext context) {
        final int count = context.getVariableOrThrow(variable);
        final Interpreter interpreter = InterpreterFactory.newInterpreter(context);
        for (int i = 0; i < count; ++i) {
            for (Statement statement : body) {
                interpreter.interpret(statement);
            }
        }
    }

    @Override
    public String toString() {
        final List<String> lines = new ArrayList<>();
        lines.add("LOOP " + variable + " DO");
        body.forEach(node -> lines.add(node.toString().indent(2).stripTrailing()));
        lines.add("END");
        return String.join("\n", lines);
    }
}
