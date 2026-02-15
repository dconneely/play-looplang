package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.statement.Statement;

final class DefaultInterpreter implements Interpreter {
    private final InterpreterContext context;

    DefaultInterpreter(final InterpreterContext context) {
        this.context = context;
    }

    @Override
    public void interpret(final Statement statement) {
        statement.interpret(context);
    }
}
