package com.davidconneely.looplang.parser;

import static com.davidconneely.looplang.statement.StatementUtils.throwUnexpectedParserException;
import static com.davidconneely.looplang.token.Token.Kind.ASSIGN;
import static com.davidconneely.looplang.token.Token.Kind.IDENTIFIER;
import static com.davidconneely.looplang.token.Token.Kind.INPUT;
import static com.davidconneely.looplang.token.Token.Kind.LPAREN;
import static com.davidconneely.looplang.token.Token.Kind.NUMBER;
import static com.davidconneely.looplang.token.Token.Kind.PLUS;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.statement.StatementFactory;
import com.davidconneely.looplang.token.Token;
import java.io.IOException;

final class DefaultParser implements Parser {
  private final Lexer lexer;
  private final ParserContext context;
  private final Token.Kind until;

  DefaultParser(final Lexer lexer, final ParserContext context, final Token.Kind until) {
    this.lexer = lexer;
    this.context = context;
    this.until = until;
  }

  @Override
  public Statement next() throws IOException {
    while (true) {
      Token token = lexer.next();
      switch (token.kind()) {
        case IDENTIFIER -> {
          return nextAssign(token);
        }
        case PRINT -> {
          lexer.pushback(token);
          return StatementFactory.newPrint(context, lexer);
        }
        case LOOP -> {
          lexer.pushback(token);
          return StatementFactory.newLoop(context, lexer);
        }
        case PROGRAM -> {
          lexer.pushback(token);
          return StatementFactory.newDefinition(context, lexer);
        }
        case SEMICOLON -> {
          continue; // in while loop, so ignore
        }
        default -> {
          if (token.kind() == until) {
            return null;
          }
          throwUnexpectedParserException("or a new statement", token, until);
        }
      }
    }
  }

  // could be an AssignNumber, AssignPlus, AssignInput or AssignCall - takes up to 4 tokens to tell
  // which :(.
  private Statement nextAssign(final Token identifier) throws IOException {
    Token assign = lexer.next();
    if (assign.kind() != ASSIGN) {
      throwUnexpectedParserException("after lvalue in assignment", assign, ASSIGN);
    }
    Token arg1 = lexer.next();
    if (arg1.kind() == NUMBER) {
      lexer.pushback(arg1);
      lexer.pushback(assign);
      lexer.pushback(identifier);
      return StatementFactory.newAssignNumber(context, lexer);
    } else if (arg1.kind() == INPUT) {
      lexer.pushback(arg1);
      lexer.pushback(assign);
      lexer.pushback(identifier);
      return StatementFactory.newAssignInput(context, lexer);
    } else if (arg1.kind() != IDENTIFIER) {
      throwUnexpectedParserException("after `:=` in assignment", arg1, NUMBER, INPUT, IDENTIFIER);
    }
    Token arg2 = lexer.next();
    if (arg2.kind() == PLUS) {
      lexer.pushback(arg2);
      lexer.pushback(arg1);
      lexer.pushback(assign);
      lexer.pushback(identifier);
      return StatementFactory.newAssignPlus(context, lexer);
    } else if (arg2.kind() == LPAREN) {
      lexer.pushback(arg2);
      lexer.pushback(arg1);
      lexer.pushback(assign);
      lexer.pushback(identifier);
      return StatementFactory.newAssignCall(context, lexer);
    }
    throwUnexpectedParserException("after rvalue identifier in assignment", arg2, PLUS, LPAREN);
    return null; // never reached
  }
}
