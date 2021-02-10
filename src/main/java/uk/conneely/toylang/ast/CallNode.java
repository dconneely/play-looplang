package uk.conneely.toylang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.interpreter.Interpreter;
import uk.conneely.toylang.interpreter.InterpreterFactory;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.parser.ParserException;
import uk.conneely.toylang.token.Token;

final class CallNode implements Node {
    private String name;
    private List<Token> paramVals;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.IDENT) {
            throw new ParserException("call: expected identifier; got " + token);
        }
        this.name = token.textValue();
        this.paramVals = new ArrayList<>();
        while (true) {
            token = tokens.next();
            if (token.kind() == Token.Kind.IDENT || token.kind() == Token.Kind.INTNUM) {
                this.paramVals.add(token);
            } else if (token.endsStmt()) {
                tokens.pushback(token);
                break;
            } else {
                throw new ParserException("call: expected identifier, number or end statement; got " + token);
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        Context subcontext = context.procedureCall(this.name, this.paramVals, 0);
        final List<Node> body = context.procedureBody(this.name);
        final Interpreter interpreter = InterpreterFactory.interpreter(subcontext);
        for (Node node : body) {
            interpreter.interpret(node);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (Token paramVal : paramVals) {
            sb.append(' ');
            sb.append(paramVal.textValue());
        }
        return sb.toString();
    }
}
