package uk.conneely.toylang.parser;

import uk.conneely.toylang.lexer.Lexer;
import uk.conneely.toylang.token.Token;

public final class ParserFactory {
    private ParserFactory() {
    }

    public static Parser parser(final Lexer tokens, final Token.Kind until) {
        return new DefaultParser(tokens, until);
    }
}
