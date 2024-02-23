package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class LoopNode implements Node {
    private final ParserContext context;
    private String variable;
    private List<Node> body;

    LoopNode(final ParserContext context) {
        this.context = context;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        nextTokenWithKind(lexer, KW_LOOP, "in loop");
        variable = nextTokenWithKind(lexer, IDENTIFIER, "as count variable in loop").value();
        Token token = lexer.next();
        if (token.kind() != KW_DO) {
            lexer.pushback(token); // `DO` is optional.
        }
        body = DefinitionNode.parseBody(lexer, context);
    }

    @Override
    public void interpret(final InterpreterContext context) {
        if (variable == null || body == null) {
            throw new InterpreterException("uninitialized loop");
        }
        final int count = context.getVariableOrThrow(variable);
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
        final List<String> lines = new ArrayList<>();
        lines.add("LOOP " + variable + " DO");
        body.forEach(node -> lines.add(node.toString().indent(2).stripTrailing()));
        lines.add("END");
        return String.join("\n", lines);
    }
}
