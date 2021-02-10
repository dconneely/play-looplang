package uk.conneely.toylang.interpreter;

import uk.conneely.toylang.ast.Node;

public interface Interpreter {
    void interpret(Node node);
}
