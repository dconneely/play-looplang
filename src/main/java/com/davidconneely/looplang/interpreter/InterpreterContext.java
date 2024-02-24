package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.statement.Statement;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Our interpreter context for a call. Note that procedure definitions and variable definitions are separate namespaces.
 * Procedure definitions are global, but variable definitions are new in each called context (apart from the parameters,
 * which are passed by reference).
 */
public interface InterpreterContext {
    String getContextName();

    boolean containsProgram(String name);
    List<Statement> getProgramBody(String name);
    List<String> getProgramParams(String name);
    void setProgram(String name, List<String> params, List<Statement> body);

    boolean containsVariable(String name);
    OptionalInt getVariable(String name);
    void setVariable(String name, int value);

    default Optional<InterpreterContext> getProgramContext(final String name, final List<String> args) {
        if (!containsProgram(name)) {
            return Optional.empty();
        }
        final InterpreterContext context = new LocalContext(name, this);
        final List<String> params = getProgramParams(name);
        final int paramc = params.size();
        final int argc = args.size();
        if (paramc != argc) {
            throw new InterpreterException("program `" + name + "` defined to take " + paramc + " params, but called with " + argc + " args");
        }
        for (int i = 0; i < argc; ++i) {
            final String parami = params.get(i);
            final String argi = args.get(i);
            context.setVariable(parami, getVariableOrThrow(argi));
        }
        return Optional.of(context);
    }

    default InterpreterContext getProgramContextOrThrow(final String name, final List<String> args) {
        return getProgramContext(name, args).orElseThrow(() -> new InterpreterException("program `" + name + "` has not been defined yet"));
    }

    default int getVariableOrThrow(final String name) {
        return getVariable(name).orElseThrow(() -> new InterpreterException("variable `" + name + "` has not been defined yet"));
    }
}
