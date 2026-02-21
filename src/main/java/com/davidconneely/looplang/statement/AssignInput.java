package com.davidconneely.looplang.statement;

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.*;

import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.token.Token;
import java.io.IOException;
import java.util.List;

record AssignInput(String variable, List<Token> printTokens) implements Statement {
  static AssignInput parse(final ParserContext context, final Lexer lexer) throws IOException {
    final String variable =
        nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in input").value();
    nextTokenWithKind(lexer, ASSIGN, "after lvalue in input");
    nextTokenWithKind(lexer, INPUT, "in input");
    final List<Token> printTokens = Print.nextPrintTokens(lexer, "in input arguments");
    return new AssignInput(variable, printTokens);
  }

  @Override
  public void interpret(final InterpreterContext context) {
    IO.print(Print.printTokensToText(printTokens, context));
    final String line = IO.readln();
    if (line == null || line.isBlank()) {
      throw new InterpreterException(
          "expected non-negative integer input for variable `" + variable + "`");
    }
    try {
      final int value = Integer.parseInt(line.strip());
      context.setVariable(variable, Math.max(0, value));
    } catch (NumberFormatException e) {
      throw new InterpreterException(
          "invalid integer input `" + line + "` for variable `" + variable + "`");
    }
  }

  @Override
  public String toString() {
    return variable + " := INPUT(" + Print.printTokensToString(printTokens) + ")";
  }
}
