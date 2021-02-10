package uk.conneely.toylang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.interpreter.Interpreter;
import uk.conneely.toylang.interpreter.InterpreterException;
import uk.conneely.toylang.interpreter.InterpreterFactory;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.parser.Parser;
import uk.conneely.toylang.parser.ParserException;
import uk.conneely.toylang.parser.ParserFactory;
import uk.conneely.toylang.token.Token;

final class ForNode implements Node {
    private Token tcount;
    private List<Node> body;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KWFOR) {
            throw new ParserException("for: expected `for`; got " + token);
        }
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENT && token.kind() != Token.Kind.INTNUM) {
            throw new ParserException("for: expected identifier or number; got " + token);
        }
        this.tcount = token;
        token = tokens.next();
        if (token.kind() != Token.Kind.OPAREN) {
            throw new ParserException("for: expected `(`; got " + token);
        }
        this.body = new ArrayList<>();
        final Parser parser = ParserFactory.parser(tokens, Token.Kind.CPAREN);
        while (true) {
            Node node = parser.next();
            if (node != null) {
                this.body.add(node);
            } else {
                break;
            }
        }
    }

    @Override
    public void interpret(final Context context) {
        final int icount;
        switch (tcount.kind()) {
            case IDENT:
                try {
                    icount = context.variable(this.tcount.textValue());
                } catch (InterpreterException e) {
                    throw new InterpreterException("for: expected defined identifier; got " + tcount, e);
                }
                break;
            case INTNUM:
                icount = tcount.intValue();
                break;
            default:
                throw new InterpreterException("for: expected identifier or number; got " + tcount);
        }
        final Interpreter interpreter = InterpreterFactory.interpreter(context);
        for (int i = 0; i < icount; ++i) {
            for (Node node : body) {
                interpreter.interpret(node);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("for ");
        switch (tcount.kind()) {
            case INTNUM:
                sb.append(tcount.intValue());
                break;
            case IDENT:
            default:
                sb.append(tcount.textValue());
                break;
        }
        sb.append(" (");
        boolean first = true;
        for (Node node : body) {
            if (!first) {
                sb.append(';');
            } else {
                first = false;
            }
            sb.append(node.toString());
        }
        sb.append(')');
        return sb.toString();
    }
}
