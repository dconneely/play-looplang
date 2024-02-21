package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.LanguageException;
import com.davidconneely.looplang.ast.Node;

final class DefaultInterpreter implements Interpreter {
    private final InterpreterContext context;

    DefaultInterpreter(final InterpreterContext context) {
        this.context = context;
    }

    @Override
    public void interpret(final Node node) {
        try {
            node.interpret(context);
        } catch (LanguageException e) {
            e.printStackTrace();
        }
    }
}
