package com.davidconneely.looplang.interpreter;

public final class InterpreterFactory {
    private InterpreterFactory() {
    }

    public static InterpreterContext newGlobalContext() {
        return new GlobalContext();
    }

    public static Interpreter newInterpreter(final InterpreterContext context) {
        return new DefaultInterpreter(context);
    }
}
