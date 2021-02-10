package uk.conneely.toylang.interpreter;

public final class InterpreterFactory {
    private InterpreterFactory() {
    }

    public static Context context() {
        return new DefaultContext();
    }

    public static Interpreter interpreter(final Context context) {
        return new DefaultInterpreter(context);
    }
}
