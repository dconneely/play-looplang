package uk.conneely.toylang.ast;

import java.io.IOException;
import uk.conneely.toylang.interpreter.Context;
import uk.conneely.toylang.lexer.Lexer;

/**
 * Each element knows how to parse and interpret itself.
 */
public interface Node {
    void parse(Lexer reader) throws IOException;

    void interpret(Context context);
}

