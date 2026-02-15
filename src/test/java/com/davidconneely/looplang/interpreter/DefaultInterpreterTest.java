package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.parser.Parser;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserFactory;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class DefaultInterpreterTest {

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

    @Test
    void assignZero_setsVariableToZero() throws IOException {
        execute("x0 := 0");
        assertEquals(0, interpreterContext.getVariable("X0").orElse(-1));
    }

    @Test
    void assignIncrement_incrementsVariable() throws IOException {
        execute("x0 := 0; x0 := x0 + 1");
        assertEquals(1, interpreterContext.getVariable("X0").orElse(-1));
    }

    @Test
    void assignIncrement_multipleIncrements() throws IOException {
        execute("x0 := 0; x0 := x0 + 1; x0 := x0 + 1; x0 := x0 + 1");
        assertEquals(3, interpreterContext.getVariable("X0").orElse(-1));
    }

    @Test
    void loop_executesBodyCorrectTimes() throws IOException {
        execute("x0 := 0; x1 := 0; x1 := x1 + 1; x1 := x1 + 1; x1 := x1 + 1; LOOP x1 DO x0 := x0 + 1 END");
        assertEquals(3, interpreterContext.getVariable("X0").orElse(-1));
    }

    @Test
    void loop_withZeroCount_doesNotExecuteBody() throws IOException {
        execute("x0 := 0; x1 := 0; LOOP x1 DO x0 := x0 + 1 END");
        assertEquals(0, interpreterContext.getVariable("X0").orElse(-1));
    }

    @Test
    void loop_countIsEvaluatedOnce() throws IOException {
        // If count was re-evaluated each iteration, this would loop forever
        // Since count is evaluated once (to 3), it should loop exactly 3 times
        execute("x0 := 0; x1 := 0; x1 := x1 + 1; x1 := x1 + 1; x1 := x1 + 1; LOOP x1 DO x0 := x0 + 1; x1 := x1 + 1 END");
        assertEquals(3, interpreterContext.getVariable("X0").orElse(-1));
        assertEquals(6, interpreterContext.getVariable("X1").orElse(-1));
    }

    @Test
    void nestedLoops_workCorrectly() throws IOException {
        // x1 = 2, x2 = 3, result should be 2 * 3 = 6
        execute("""
                x0 := 0
                x1 := 0; x1 := x1 + 1; x1 := x1 + 1
                x2 := 0; x2 := x2 + 1; x2 := x2 + 1; x2 := x2 + 1
                LOOP x1 DO
                    LOOP x2 DO
                        x0 := x0 + 1
                    END
                END
                """);
        assertEquals(6, interpreterContext.getVariable("X0").orElse(-1));
    }

    @Test
    void programDefinition_registersProgram() throws IOException {
        execute("PROGRAM ADD(x1, x2) DO x0 := x0 + 1 END");
        assertTrue(interpreterContext.containsProgram("ADD"));
    }

    @Test
    void programCall_executesProgram() throws IOException {
        execute("""
                PROGRAM ASSIGN(x1) DO
                    x0 := 0
                    LOOP x1 DO
                        x0 := x0 + 1
                    END
                END
                x1 := 0; x1 := x1 + 1; x1 := x1 + 1; x1 := x1 + 1; x1 := x1 + 1; x1 := x1 + 1
                result := ASSIGN(x1)
                """);
        assertEquals(5, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    void programCall_returnsX0Value() throws IOException {
        execute("""
                PROGRAM FIVE() DO
                    x0 := 0
                    x0 := x0 + 1; x0 := x0 + 1; x0 := x0 + 1; x0 := x0 + 1; x0 := x0 + 1
                END
                result := FIVE()
                """);
        assertEquals(5, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    void programCall_localVariablesDoNotAffectGlobal() throws IOException {
        execute("""
                PROGRAM SETX1() DO
                    x1 := 0
                    x1 := x1 + 1; x1 := x1 + 1; x1 := x1 + 1
                    x0 := 0
                    LOOP x1 DO x0 := x0 + 1 END
                END
                x1 := 0
                result := SETX1()
                """);
        assertEquals(0, interpreterContext.getVariable("X1").orElse(-1));
        assertEquals(3, interpreterContext.getVariable("RESULT").orElse(-1));
    }

    @Test
    void undefinedVariable_throwsException() {
        assertThrows(Exception.class, () -> execute("x0 := undefined + 1"));
    }

    @Test
    void redefinedProgram_throwsException() {
        assertThrows(InterpreterException.class, () -> execute("""
                PROGRAM FOO() DO x0 := 0 END
                PROGRAM FOO() DO x0 := 0 END
                """));
    }

    @Test
    void loopWithUndefinedVariable_throws() {
        // When looping on an undefined variable, it throws an exception
        assertThrows(InterpreterException.class, () -> execute("x0 := 0; LOOP undefined DO x0 := x0 + 1 END"));
    }

    @Test
    void multipleStatementsSeparatedBySemicolons() throws IOException {
        execute("x0 := 0; x1 := 0; x2 := 0; x0 := x0 + 1; x1 := x1 + 1; x2 := x2 + 1");
        assertEquals(1, interpreterContext.getVariable("X0").orElse(-1));
        assertEquals(1, interpreterContext.getVariable("X1").orElse(-1));
        assertEquals(1, interpreterContext.getVariable("X2").orElse(-1));
    }
}
