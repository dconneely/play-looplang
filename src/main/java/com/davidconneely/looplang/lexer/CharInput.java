package com.davidconneely.looplang.lexer;

import java.io.IOException;

public interface CharInput {
    void pushback(int ch);

    /**
     * -1 = EOF
     */
    int next() throws IOException;
}
