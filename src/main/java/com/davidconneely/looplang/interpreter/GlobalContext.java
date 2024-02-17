package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.ast.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GlobalContext implements Context {
    private final Map<String, List<String>> programParams;
    private final Map<String, List<Node>> programBodies;
    private final Map<String, Integer> variables;

    GlobalContext() {
        this.programParams = new HashMap<>();
        this.programBodies = new HashMap<>();
        this.variables = new HashMap<>();
    }

    @Override
    public boolean isProgramDefined(final String programName) {
        return programParams.containsKey(programName) && programBodies.containsKey(programName);
    }

    @Override
    public void defineProgram(final String programName, final List<String> params, final List<Node> body) {
        if (isProgramDefined(programName)) {
            throw new InterpreterException("program `" + programName + "` has already been defined");
        }
        programParams.put(programName, params);
        programBodies.put(programName, body);
    }

    @Override
    public List<Node> getProgramBody(final String name) {
        if (!isProgramDefined(name)) {
            throw new InterpreterException("program `" + name + "` has not been defined yet (body requested)");
        }
        return programBodies.get(name);
    }

    @Override
    public List<String> getProgramParams(final String name) {
        if (!isProgramDefined(name)) {
            throw new InterpreterException("program `" + name + "` has not been defined yet (body requested)");
        }
        return programParams.get(name);
    }

    @Override
    public String getName() {
        return "<global definitions>";
    }

    @Override
    public int getVariable(final String variableName) {
        if (!variables.containsKey(variableName)) {
            throw new InterpreterException("global variable `" + variableName + "` has not been defined yet");
        }
        return variables.get(variableName);
    }

    @Override
    public void setVariable(final String variableName, final int newValue) {
        variables.put(variableName, newValue);
    }
}
