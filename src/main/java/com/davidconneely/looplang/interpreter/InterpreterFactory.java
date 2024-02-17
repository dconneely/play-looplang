package com.davidconneely.looplang.interpreter;

public final class InterpreterFactory {
    private InterpreterFactory() {
    }

    public static Context newGlobalContext() {
        return new GlobalContext();
    }

    public static Interpreter newInterpreter(final Context context) {
        return new DefaultInterpreter(context);
    }
}
