package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.ast.Node;

public interface Interpreter {
    void interpret(Node node);
}
