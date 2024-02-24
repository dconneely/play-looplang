package com.davidconneely.looplang.lexer;

import java.io.IOException;

public interface CharInput {
    void pushback(int ch);

    /**
     * @return -1 for EOF.
     */
    int next() throws IOException;
}
