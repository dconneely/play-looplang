package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.statement.Statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

final class LocalContext implements InterpreterContext {
    private final InterpreterContext parent;
    private final String name;
    private final Map<String, Integer> variables;

    LocalContext(final String name, final InterpreterContext parent) {
        this.parent = parent;
        this.name = name;
        this.variables = new HashMap<>();
    }

    @Override
    public String getContextName() {
        return name;
    }

    @Override
    public boolean containsProgram(final String name) {
        return parent.containsProgram(name);
    }

    @Override
    public List<Statement> getProgramBody(final String name) {
        return parent.getProgramBody(name);
    }

    @Override
    public List<String> getProgramParams(final String name) {
        return parent.getProgramParams(name);
    }

    @Override
    public void setProgram(final String name, final List<String> params, final List<Statement> body) {
        // currently the language syntax definition doesn't disallow this, so catch at runtime.
        // TODO: should probably be part of the language syntax, so can be a ParserException.
        throw new InterpreterException("cannot nest program `" + name + "` inside outer program `" + this.name + "`");
    }

    @Override
    public boolean containsVariable(final String name) {
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
