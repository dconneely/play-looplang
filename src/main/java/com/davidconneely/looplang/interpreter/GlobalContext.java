package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.ast.Node;

import java.util.*;

final class GlobalContext implements Context {
    private final Map<String, List<String>> programParams;
    private final Map<String, List<Node>> programBodies;
    private final Map<String, Integer> variables;

    GlobalContext() {
        this.programParams = new HashMap<>();
        this.programBodies = new HashMap<>();
        this.variables = new HashMap<>();
    }

    @Override
    public String getContextName() {
        return "<global>";
    }


    @Override
    public boolean containsProgram(final String name) {
        return programParams.containsKey(name) && programBodies.containsKey(name);
    }

    @Override
    public List<Node> getProgramBody(final String name) {
        return programBodies.get(name);
    }

    @Override
    public List<String> getProgramParams(final String name) {
        return programParams.get(name);
    }

    @Override
    public void setProgram(final String programName, final List<String> params, final List<Node> body) {
        if (containsProgram(programName)) {
            throw new InterpreterException("program `" + programName + "` has already been defined");
        }
        programParams.put(programName, params);
        programBodies.put(programName, body);
    }

    @Override
    public boolean containsVariable(String name) {
        return variables.containsKey(name);
    }

    @Override
    public OptionalInt getVariable(final String name) {
        Integer value = variables.get(name);
        return value != null ? OptionalInt.of(value) : OptionalInt.empty(); // like OptionalInt.ofNullable
    }

    @Override
    public void setVariable(final String name, final int value) {
        variables.put(name, value);
    }
}
