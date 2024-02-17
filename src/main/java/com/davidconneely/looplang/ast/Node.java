package com.davidconneely.looplang.ast;

import java.io.IOException;
import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.lexer.Lexer;

/**
 * Each element knows how to parse and interpret itself.
 */
public interface Node {
    void parse(Lexer lexer) throws IOException;

    void interpret(Context context);
}

