package uk.conneely.toylang.lexer;

import java.io.IOException;
import uk.conneely.toylang.token.Token;

public interface Lexer {
    void pushback(Token token);

    Token next() throws IOException;
}
