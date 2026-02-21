package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.LocatedException;

public class InterpreterException extends LocatedException {
  public InterpreterException(final String message) {
    super(message);
  }
}
