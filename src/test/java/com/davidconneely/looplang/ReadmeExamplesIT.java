package com.davidconneely.looplang;

import static org.junit.jupiter.api.Assertions.*;

import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterContext;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.token.Token;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Integration tests based on examples from the README.md file. */
class ReadmeExamplesIT {

  private ParserContext parserContext;
  private InterpreterContext interpreterContext;
  private Interpreter interpreter;

  @BeforeEach
  void setUp() {
    Location location = Location.newFile("<test>");
    parserContext = ParserFactory.newContext(location);
    interpreterContext = InterpreterFactory.newGlobalContext(parserContext);
    interpreter = InterpreterFactory.newInterpreter(interpreterContext);
  }

  private void execute(String code) throws IOException {
    Location location = Location.newFile("<test>");
    Lexer lexer = LexerFactory.newLexer(location, new StringReader(code));
    Parser parser = ParserFactory.newParser(lexer, parserContext, Token.Kind.EOF);
    Statement stmt;
    while ((stmt = parser.next()) != null) {
      interpreter.interpret(stmt);
    }
  }

  @Nested
  @DisplayName("README Basic Statements")
  class BasicStatements {

    @Test
    @DisplayName("Statement 1: varname := 0")
    void assignZero() throws IOException {
      execute("x := 0");
      assertEquals(0, interpreterContext.getVariable("X").orElse(-1));
    }

    @Test
    @DisplayName("Statement 2: varname := varname + 1")
    void assignIncrement() throws IOException {
      execute("x := 0; x := x + 1");
      assertEquals(1, interpreterContext.getVariable("X").orElse(-1));
    }

    @Test
    @DisplayName("Statement 3: statement; statement (sequencing)")
    void sequencing() throws IOException {
      execute("a := 0; b := 0; a := a + 1; b := b + 1; b := b + 1");
      assertEquals(1, interpreterContext.getVariable("A").orElse(-1));
      assertEquals(2, interpreterContext.getVariable("B").orElse(-1));
    }

    @Test
    @DisplayName("Statement 4: LOOP varname DO statement END")
    void loopStatement() throws IOException {
      execute(
          "count := 0; count := count + 1; count := count + 1; count := count + 1; "
              + "result := 0; LOOP count DO result := result + 1 END");
      assertEquals(3, interpreterContext.getVariable("RESULT").orElse(-1));
    }
  }

  @Nested
  @DisplayName("README Extended Statements")
  class ExtendedStatements {

    @Test
    @DisplayName("Statement 7 & 8: PROGRAM definition and call")
    void programDefinitionAndCall() throws IOException {
      execute(
          """
                    PROGRAM ASSIGN(x1) DO
                        x0 := 0
                        LOOP x1 DO
                            x0 := x0 + 1
                        END
                    END
                    arg := 0; arg := arg + 1; arg := arg + 1; arg := arg + 1; arg := arg + 1; arg := arg + 1
                    result := ASSIGN(arg)
                    """);
      assertEquals(5, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    @DisplayName("DO keyword is optional in LOOP")
    void loopWithoutDo() throws IOException {
      execute(
          "count := 0; count := count + 1; count := count + 1; "
              + "result := 0; LOOP count result := result + 1 END");
      assertEquals(2, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    @DisplayName("DO keyword is optional in PROGRAM")
    void programWithoutDo() throws IOException {
      execute(
          """
                    PROGRAM DOUBLE(x1)
                        x0 := 0
                        LOOP x1 DO x0 := x0 + 1; x0 := x0 + 1 END
                    END
                    arg := 0; arg := arg + 1; arg := arg + 1; arg := arg + 1
                    result := DOUBLE(arg)
                    """);
      assertEquals(6, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    @DisplayName("Semicolon statement separator is optional")
    void optionalSemicolon() throws IOException {
      execute(
          """
                    x := 0
                    x := x + 1
                    x := x + 1
                    """);
      assertEquals(2, interpreterContext.getVariable("X").orElse(-1));
    }
  }

  @Nested
  @DisplayName("README Limitations and Rules")
  class LimitationsAndRules {

    @Test
    @DisplayName("Programs can be defined only once")
    void programCannotBeRedefined() {
      assertThrows(
          Exception.class,
          () ->
              execute(
                  """
                    PROGRAM FOO() DO x0 := 0 END
                    PROGRAM FOO() DO x0 := 0 END
                    """));
    }

    @Test
    @DisplayName("Statement can only call programs defined before it")
    void cannotCallUndefinedProgram() {
      assertThrows(ParserException.class, () -> execute("result := UNDEFINED()"));
    }

    @Test
    @DisplayName("Self-recursion is not possible")
    void selfRecursionNotPossible() {
      // A program cannot call itself because it's not registered until after parsing
      assertThrows(
          ParserException.class,
          () ->
              execute(
                  """
                    PROGRAM RECURSE(x1) DO
                        x0 := RECURSE(x1)
                    END
                    """));
    }

    @Test
    @DisplayName("Mutual recursion is not possible")
    void mutualRecursionNotPossible() {
      // Program A cannot call Program B if B is defined after A
      assertThrows(
          ParserException.class,
          () ->
              execute(
                  """
                    PROGRAM A(x1) DO
                        x0 := B(x1)
                    END
                    PROGRAM B(x1) DO
                        x0 := x1
                    END
                    """));
    }

    @Test
    @DisplayName("Variables cannot be referred to before defined (except in PRINT)")
    void undefinedVariableThrows() {
      assertThrows(Exception.class, () -> execute("x := undefined + 1"));
    }

    @Test
    @DisplayName("x0 in PROGRAM is initialised to 0")
    void x0InitialisedInProgram() throws IOException {
      execute(
          """
                    PROGRAM RETURNZERO() DO
                        # x0 is implicitly 0, we don't set it
                    END
                    result := RETURNZERO()
                    """);
      assertEquals(0, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    @DisplayName("Parentheses around argument lists are required")
    void parenthesesRequired() {
      // This should fail to parse without parentheses
      assertThrows(Exception.class, () -> execute("PROGRAM FOO DO x0 := 0 END"));
    }
  }
}
