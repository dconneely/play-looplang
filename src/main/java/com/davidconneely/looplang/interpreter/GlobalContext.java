package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.statement.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

final class GlobalContext implements InterpreterContext {
  private final ParserContext parserContext;
  private final Map<String, List<String>> programParams;
  private final Map<String, List<Statement>> programBodies;
  private final Map<String, Integer> variables;

  GlobalContext(final ParserContext parserContext) {
    this.parserContext = parserContext;
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
  public List<Statement> getProgramBody(final String name) {
    return programBodies.get(name);
  }

  @Override
  public List<String> getProgramParams(final String name) {
    return programParams.get(name);
  }

  @Override
  public void setProgram(
      final String programName, final List<String> params, final List<Statement> body) {
    if (containsProgram(programName)) {
      throw new InterpreterException("program `" + programName + "` has already been defined");
    }
    programParams.put(programName, params);
    programBodies.put(programName, body);
    parserContext.addDefinedProgram(programName);
  }

  @Override
  public boolean containsVariable(final String name) {
    return variables.containsKey(name);
  }

  @Override
  public OptionalInt getVariable(final String name) {
    Integer value = variables.get(name);
    return value != null
        ? OptionalInt.of(value)
        : OptionalInt.empty(); // like OptionalInt.ofNullable
  }

  @Override
  public void setVariable(final String name, final int value) {
    variables.put(name, value);
  }
}
