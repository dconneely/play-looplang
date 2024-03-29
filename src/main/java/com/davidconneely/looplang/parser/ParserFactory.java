package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.token.Token;

public final class ParserFactory {
    private ParserFactory() {
    }

    public static Parser newParser(final Lexer lexer, final ParserContext context, final Token.Kind until) {
        return new DefaultParser(lexer, context, until);
    }

    public static ParserContext newContext(final Location location) {
        return new ParserContext(location);
    }
}
