package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.parser.ParserContext;

public final class InterpreterFactory {
  private InterpreterFactory() {}

  public static InterpreterContext newGlobalContext(final ParserContext parserContext) {
    return new GlobalContext(parserContext);
  }

  public static Interpreter newInterpreter(final InterpreterContext interpreterContext) {
    return new DefaultInterpreter(interpreterContext);
  }
}
