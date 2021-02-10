package uk.conneely.toylang.ast;

import java.io.IOException;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.parser.ParserException;
import uk.conneely.toylang.token.Token;

final class StzNode implements Node {
    private String name;

    @Override
    public void parse(final Lexer tokens) throws IOException {
        Token token = tokens.next();
        if (token.kind() != Token.Kind.KWSTZ) {
            throw new ParserException("stz: expected `stz`; got " + token);
        }
        token = tokens.next();
        if (token.kind() != Token.Kind.IDENT) {
            throw new ParserException("stz: expected identifier; got " + token);
        }
        this.name = token.textValue();
    }

    @Override
    public void interpret(final Context context) {
        context.variable(this.name, 0);
    }

    @Override
    public String toString() {
        return "stz " + name;
    }
}
