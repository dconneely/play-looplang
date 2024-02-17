package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.ast.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class LocalContext implements Context {
    private final Context parent;
    private final String name;
    private final Map<String, Integer> variables;

    LocalContext(String name, Context parent) {
        this.parent = parent;
        this.name = name;
        this.variables = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVariable(final String variableName) {
        if (!variables.containsKey(variableName)) {
            throw new InterpreterException("local variable `" + variableName + "` has not been defined yet");
        }
        return variables.get(variableName);
    }

    @Override
    public void setVariable(final String variableName, final int newValue) {
        variables.put(variableName, newValue);
    }

    @Override
    public boolean containsProgram(String name) {
        return parent.containsProgram(name);
    }

    @Override
    public List<Node> getProgramBody(String name) {
        return parent.getProgramBody(name);
    }

    @Override
    public List<String> getProgramParams(String name) {
        return parent.getProgramParams(name);
    }

    @Override
    public void setProgram(String name, List<String> params, List<Node> body) {
        throw new InterpreterException("cannot define a nested program (`" + name + "` within `" + this.name + "`)");
    }
}
