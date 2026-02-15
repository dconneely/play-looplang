package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.lexer.LexerFactory;
import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.statement.Statement;
import com.davidconneely.looplang.token.Token;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class DefaultParserTest {

    private Parser createParser(String input) {
        Location location = Location.newFile("<test>");
        Lexer lexer = LexerFactory.newLexer(location, new StringReader(input));
        ParserContext context = ParserFactory.newContext(location);
        return ParserFactory.newParser(lexer, context, Token.Kind.EOF);
    }

    @Test
    void emptyInput_returnsNull() throws IOException {
        Parser parser = createParser("");
        assertNull(parser.next());
    }

    @Test
    void assignZero_parsesCorrectly() throws IOException {
        Parser parser = createParser("x0 := 0");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().contains("x0 := 0"));
    }

    @Test
    void assignIncrement_parsesCorrectly() throws IOException {
        Parser parser = createParser("x0 := x0 + 1");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().contains("x0 := x0 + 1"));
    }

    @Test
    void print_parsesCorrectly() throws IOException {
        Parser parser = createParser("PRINT(\"hello\", x0, 42)");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().startsWith("PRINT("));
    }

    @Test
    void loop_parsesCorrectly() throws IOException {
        Parser parser = createParser("LOOP x1 DO x0 := x0 + 1 END");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().startsWith("LOOP"));
    }

    @Test
    void loop_withoutDo_parsesCorrectly() throws IOException {
        Parser parser = createParser("LOOP x1 x0 := x0 + 1 END");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().startsWith("LOOP"));
    }

    @Test
    void definition_parsesCorrectly() throws IOException {
        Parser parser = createParser("PROGRAM ADD(x1, x2) DO x0 := x0 + 1 END");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().startsWith("PROGRAM ADD"));
    }

    @Test
    void definition_withoutDo_parsesCorrectly() throws IOException {
        Parser parser = createParser("PROGRAM ADD(x1, x2) x0 := x0 + 1 END");
        Statement stmt = parser.next();
        assertNotNull(stmt);
        assertTrue(stmt.toString().startsWith("PROGRAM ADD"));
    }

    @Test
    void multipleStatements_withSemicolon_parseCorrectly() throws IOException {
        Parser parser = createParser("x0 := 0; x0 := x0 + 1");
        Statement stmt1 = parser.next();
        assertNotNull(stmt1);
        assertTrue(stmt1.toString().contains(":= 0"));
        Statement stmt2 = parser.next();
        assertNotNull(stmt2);
        assertTrue(stmt2.toString().contains("+ 1"));
    }

    @Test
    void multipleStatements_withoutSemicolon_parseCorrectly() throws IOException {
        Parser parser = createParser("x0 := 0\nx0 := x0 + 1");
        Statement stmt1 = parser.next();
        assertNotNull(stmt1);
        Statement stmt2 = parser.next();
        assertNotNull(stmt2);
    }

    @Test
    void callToUndefinedProgram_throwsException() {
        Parser parser = createParser("x0 := UNKNOWN(x1)");
        assertThrows(ParserException.class, parser::next);
    }

    @Test
    void callToDefinedProgram_parsesCorrectly() throws IOException {
        Location location = Location.newFile("<test>");
        Lexer lexer = LexerFactory.newLexer(location, new StringReader(
                "PROGRAM ADD(x1, x2) DO x0 := x0 + 1 END; x0 := ADD(a, b)"));
        ParserContext context = ParserFactory.newContext(location);
        Parser parser = ParserFactory.newParser(lexer, context, Token.Kind.EOF);

        // Parse and interpret the definition to register it
        Statement def = parser.next();
        assertNotNull(def);
        context.addDefinedProgram("ADD");

        // Now the call should parse
        Statement call = parser.next();
        assertNotNull(call);
        assertTrue(call.toString().contains("ADD"));
    }

    @Test
    void nestedLoop_parsesCorrectly() throws IOException {
        Parser parser = createParser("""
                LOOP x1 DO
                    LOOP x2 DO
                        x0 := x0 + 1
                    END
                END
                """);
        Statement stmt = parser.next();
        assertNotNull(stmt);
        String str = stmt.toString();
        // Should contain nested LOOP structures
        assertTrue(str.contains("LOOP"));
        assertTrue(str.contains("END"));
    }
}
