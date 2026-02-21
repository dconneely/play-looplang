package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.statement.Statement;

public interface Interpreter {
  void interpret(Statement statement);
}
