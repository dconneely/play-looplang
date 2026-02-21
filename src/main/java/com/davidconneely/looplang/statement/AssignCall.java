package com.davidconneely.looplang.statement;

import static com.davidconneely.looplang.statement.StatementUtils.nextTokenWithKind;
import static com.davidconneely.looplang.token.Token.Kind.ASSIGN;
import static com.davidconneely.looplang.token.Token.Kind.IDENTIFIER;

import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.token.Token;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @param variable variable name on left of `:=` sign
 * @param program called program name to right of `:=` sign
 * @param args variable names of the args to the call
 */
record AssignCall(String variable, String program, List<String> args) implements Statement {
  static AssignCall parse(final ParserContext context, final Lexer lexer) throws IOException {
    final String variable =
        nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in call assignment").value();
    nextTokenWithKind(lexer, ASSIGN, "after lvalue in call assignment");
    Token token = nextTokenWithKind(lexer, IDENTIFIER, "as program name in call");
    final String program = token.value();
    context.checkProgramIsDefined(program, token);
    final List<String> args = Definition.nextTokensAsCSVNames(lexer, "in args list in call");
    return new AssignCall(variable, program, args);
  }

  @Override
  public void interpret(final InterpreterContext context) {
    final InterpreterContext subcontext = context.getProgramContextOrThrow(program, args);
    if (subcontext.getVariable("X0").isEmpty()) {
      subcontext.setVariable("X0", 0);
    }
    final List<Statement> body = context.getProgramBody(program);
    final Interpreter interpreter = InterpreterFactory.newInterpreter(subcontext);
    for (Statement statement : body) {
      interpreter.interpret(statement);
    }
    final int x0 = subcontext.getVariable("X0").orElse(0);
    context.setVariable(variable, x0);
  }

  @Override
  public String toString() {
    return variable.toLowerCase(Locale.ROOT)
        + " := "
        + program.toUpperCase(Locale.ROOT)
        + '('
        + args.stream().map(arg -> arg.toLowerCase(Locale.ROOT)).collect(Collectors.joining(", "))
        + ')';
  }
}
