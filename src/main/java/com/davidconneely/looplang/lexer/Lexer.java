package com.davidconneely.looplang.lexer;

import java.io.IOException;
import com.davidconneely.looplang.token.Token;

public interface Lexer {
    void pushback(Token token);

    Token next() throws IOException;
}
