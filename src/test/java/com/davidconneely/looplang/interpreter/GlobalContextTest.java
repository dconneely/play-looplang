package com.davidconneely.looplang.interpreter;

import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.parser.ParserContext;
import com.davidconneely.looplang.parser.ParserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalContext")
class GlobalContextTest {

    private ParserContext parserContext;
    private InterpreterContext context;

    @BeforeEach
    void setUp() {
        Location location = Location.newFile("<test>");
        parserContext = ParserFactory.newContext(location);
        context = InterpreterFactory.newGlobalContext(parserContext);
    }

    @Nested
    @DisplayName("Variables")
    class Variables {

        @Test
        @DisplayName("returns empty for undefined variable")
        void undefinedVariable() {
            OptionalInt result = context.getVariable("UNDEFINED");
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns value for defined variable")
        void definedVariable() {
            context.setVariable("X", 42);
            OptionalInt result = context.getVariable("X");
            assertTrue(result.isPresent());
            assertEquals(42, result.getAsInt());
        }

        @Test
        @DisplayName("containsVariable returns false for undefined")
        void containsUndefined() {
            assertFalse(context.containsVariable("UNDEFINED"));
        }

        @Test
        @DisplayName("containsVariable returns true for defined")
        void containsDefined() {
            context.setVariable("X", 0);
            assertTrue(context.containsVariable("X"));
        }

        @Test
        @DisplayName("can overwrite variable value")
        void overwriteVariable() {
            context.setVariable("X", 10);
            context.setVariable("X", 20);
            assertEquals(20, context.getVariable("X").orElse(-1));
        }

        @Test
        @DisplayName("getVariableOrThrow throws for undefined")
        void getOrThrowUndefined() {
            assertThrows(InterpreterException.class, () -> context.getVariableOrThrow("UNDEFINED"));
        }

        @Test
        @DisplayName("getVariableOrThrow returns value for defined")
        void getOrThrowDefined() {
            context.setVariable("X", 42);
            assertEquals(42, context.getVariableOrThrow("X"));
        }
    }

    @Nested
    @DisplayName("Programs")
    class Programs {

        @Test
        @DisplayName("returns false for undefined program")
        void undefinedProgram() {
            assertFalse(context.containsProgram("UNDEFINED"));
        }

        @Test
        @DisplayName("returns true for defined program")
        void definedProgram() {
            context.setProgram("FOO", List.of("X1"), List.of());
            assertTrue(context.containsProgram("FOO"));
        }

        @Test
        @DisplayName("stores and retrieves program params")
        void programParams() {
            context.setProgram("ADD", List.of("X1", "X2"), List.of());
            List<String> params = context.getProgramParams("ADD");
            assertEquals(2, params.size());
            assertEquals("X1", params.get(0));
            assertEquals("X2", params.get(1));
        }

        @Test
        @DisplayName("stores and retrieves program body")
        void programBody() {
            context.setProgram("FOO", List.of(), List.of());
            List<?> body = context.getProgramBody("FOO");
            assertNotNull(body);
            assertTrue(body.isEmpty());
        }

        @Test
        @DisplayName("throws on program redefinition")
        void redefinition() {
            context.setProgram("FOO", List.of(), List.of());
            assertThrows(InterpreterException.class, () -> context.setProgram("FOO", List.of(), List.of()));
        }
    }

    @Nested
    @DisplayName("Context name")
    class ContextName {

        @Test
        @DisplayName("returns global context name")
        void globalName() {
            assertEquals("<global>", context.getContextName());
        }
    }
}
