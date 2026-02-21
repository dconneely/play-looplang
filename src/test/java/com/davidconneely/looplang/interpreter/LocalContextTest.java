package com.davidconneely.looplang.interpreter;

import static org.junit.jupiter.api.Assertions.*;

import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LocalContext (via getProgramContext)")
class LocalContextTest {

  private InterpreterContext globalContext;

  @BeforeEach
  void setUp() {
    Location location = Location.newFile("<test>");
    ParserContext parserContext = ParserFactory.newContext(location);
    globalContext = InterpreterFactory.newGlobalContext(parserContext);
    // Define a program with two params
    globalContext.setProgram("MYPROG", List.of("X1", "X2"), List.of());
  }

  @Nested
  @DisplayName("Context creation")
  class ContextCreation {

    @Test
    @DisplayName("returns empty for undefined program")
    void undefinedProgram() {
      Optional<InterpreterContext> ctx = globalContext.getProgramContext("UNDEFINED", List.of());
      assertTrue(ctx.isEmpty());
    }

    @Test
    @DisplayName("returns context for defined program")
    void definedProgram() {
      globalContext.setVariable("A", 10);
      globalContext.setVariable("B", 20);
      Optional<InterpreterContext> ctx =
          globalContext.getProgramContext("MYPROG", List.of("A", "B"));
      assertTrue(ctx.isPresent());
    }

    @Test
    @DisplayName("throws when arg count mismatches param count")
    void argCountMismatch() {
      globalContext.setVariable("A", 10);
      assertThrows(
          InterpreterException.class,
          () -> globalContext.getProgramContext("MYPROG", List.of("A")));
    }

    @Test
    @DisplayName("throws when arg variable is undefined")
    void undefinedArg() {
      assertThrows(
          InterpreterException.class,
          () -> globalContext.getProgramContext("MYPROG", List.of("UNDEFINED1", "UNDEFINED2")));
    }
  }

  @Nested
  @DisplayName("Variable passing")
  class VariablePassing {

    @Test
    @DisplayName("initialises params from global variables")
    void initialisesParams() {
      globalContext.setVariable("A", 10);
      globalContext.setVariable("B", 20);
      InterpreterContext localContext =
          globalContext.getProgramContext("MYPROG", List.of("A", "B")).orElseThrow();
      assertEquals(10, localContext.getVariable("X1").orElse(-1));
      assertEquals(20, localContext.getVariable("X2").orElse(-1));
    }

    @Test
    @DisplayName("local variables don't affect global context")
    void localIsolation() {
      globalContext.setVariable("A", 0);
      globalContext.setVariable("B", 0);
      InterpreterContext localContext =
          globalContext.getProgramContext("MYPROG", List.of("A", "B")).orElseThrow();
      localContext.setVariable("LOCAL", 42);
      assertTrue(localContext.containsVariable("LOCAL"));
      assertFalse(globalContext.containsVariable("LOCAL"));
    }
  }

  @Nested
  @DisplayName("Program access")
  class ProgramAccess {

    @Test
    @DisplayName("local context can see global programs")
    void delegatesProgramLookup() {
      globalContext.setVariable("A", 0);
      globalContext.setVariable("B", 0);
      InterpreterContext localContext =
          globalContext.getProgramContext("MYPROG", List.of("A", "B")).orElseThrow();
      assertTrue(localContext.containsProgram("MYPROG"));
    }

    @Test
    @DisplayName("nested program definitions are not allowed")
    void nestedProgramNotAllowed() {
      globalContext.setVariable("A", 0);
      globalContext.setVariable("B", 0);
      InterpreterContext localContext =
          globalContext.getProgramContext("MYPROG", List.of("A", "B")).orElseThrow();
      assertThrows(
          InterpreterException.class,
          () -> localContext.setProgram("NEWPROG", List.of(), List.of()));
    }
  }

  @Nested
  @DisplayName("OrThrow variants")
  class OrThrowVariants {

    @Test
    @DisplayName("getProgramContextOrThrow throws for undefined program")
    void throwsForUndefinedProgram() {
      assertThrows(
          InterpreterException.class,
          () -> globalContext.getProgramContextOrThrow("UNDEFINED", List.of()));
    }

    @Test
    @DisplayName("getProgramContextOrThrow returns context for defined program")
    void returnsForDefinedProgram() {
      globalContext.setVariable("A", 0);
      globalContext.setVariable("B", 0);
      InterpreterContext ctx = globalContext.getProgramContextOrThrow("MYPROG", List.of("A", "B"));
      assertNotNull(ctx);
      assertEquals("MYPROG", ctx.getContextName());
    }
  }
}
