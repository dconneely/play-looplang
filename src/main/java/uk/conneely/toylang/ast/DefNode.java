package uk.conneely.toylang.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.parser.Parser;
import uk.conneely.toylang.parser.ParserException;
import uk.conneely.toylang.parser.ParserFactory;
import uk.conneely.toylang.token.Token;

final class DefNode implements Node {
    private String name;
    private List<String> paramDefs;
    private List<Node> body;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KWDEF) {
            throw new ParserException("def: expected `def`; got " + token);
        }
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENT) {
            throw new ParserException("def: expected identifier; got " + token);
        }
        this.name = token.textValue();
        this.paramDefs = new ArrayList<>();
        while (true) {
            token = tokens.next();
            if (token.kind() == Token.Kind.IDENT) {
                this.paramDefs.add(token.textValue());
            } else if (token.kind() == Token.Kind.OPAREN) {
                break;
            } else {
                throw new ParserException("def: expected identifier or `(`; got " + token);
            }
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
        context.procedure(name, paramDefs, body);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("def ");
        sb.append(name);
        for (String paramDef : paramDefs) {
            sb.append(' ');
            sb.append(paramDef);
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
