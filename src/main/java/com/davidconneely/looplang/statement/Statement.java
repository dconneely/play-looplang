package com.davidconneely.looplang.statement;

import com.davidconneely.looplang.interpreter.InterpreterContext;

/** Each element knows how to parse and interpret itself. */
public sealed interface Statement
    permits AssignCall, AssignIncrement, AssignInput, AssignZero, Definition, Loop, Print {
  /*
  static Statement parse(ParserContext context, Lexer lexer) throws IOException {
      throw new UnsupportedOperationException("should call factory method in an implementing class, not the interface");
  }
  */

  void interpret(InterpreterContext context);
}
