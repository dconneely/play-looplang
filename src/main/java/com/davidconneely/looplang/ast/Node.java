package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.lexer.Lexer;

import java.io.IOException;

/**
 * Each element knows how to parse and interpret itself.
 */
public interface Node {
    void parse(Lexer lexer) throws IOException;
    void interpret(Context context);
}

