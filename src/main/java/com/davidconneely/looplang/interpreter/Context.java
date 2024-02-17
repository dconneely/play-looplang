package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.ast.Node;

import java.util.List;

/**
 * Our interpreter context for a call. Note that procedure definitions and variable definitions are separate namespaces.
 * Procedure definitions are global, but variable definitions are new in each called context (apart from the parameters,
 * which are passed by reference).
 */
public interface Context {
    String getName();
    int getVariable(String variableName);
    void setVariable(String variableName, int newValue);

    boolean containsProgram(String name);
    void setProgram(String name, List<String> params, List<Node> body);
    List<Node> getProgramBody(String name);
    List<String> getProgramParams(String name);

    default Context getProgramContext(String name, final List<String> args) {
        if (!containsProgram(name)) {
            throw new InterpreterException("program `" + name + "` has not been defined yet (context requested)");
        }
        Context context = new LocalContext(name, this);
        final List<String> params = getProgramParams(name);
        final int paramc = params.size();
        final int argc = args.size();
        if (paramc != argc) {
            throw new InterpreterException("program `" + name + "` defined to take " + paramc + " params, but called with " + argc + " args");
        }
        for (int i = 0; i < argc; ++i) {
            final String parami = params.get(i);
            final String argi = args.get(i);
            context.setVariable(parami, getVariable(argi));
        }
        return context;
    }
}
