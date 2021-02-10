package uk.conneely.toylang.ast;

import java.io.IOException;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.parser.ParserException;
import uk.conneely.toylang.token.Token;

final class IncNode implements Node {
    private String name;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KWINC) {
            throw new ParserException("inc: expected `inc`; got " + token);
        }
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENT) {
            throw new ParserException("inc: expected identifier; got " + token);
        }
        this.name = token.textValue();
    }

    @Override
    public void interpret(final Context context) {
        context.variable(this.name, context.variable(this.name) + 1);
    }

    @Override
    public String toString() {
        return "inc " + name;
    }
}
