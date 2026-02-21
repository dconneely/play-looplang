package com.davidconneely.looplang.lexer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Location")
class LocationTest {

  @Nested
  @DisplayName("Creation")
  class Creation {

    @Test
    @DisplayName("newFile creates location with line 1")
    void newFile() {
      Location location = Location.newFile("test.loop");
      assertEquals("test.loop", location.filename());
      assertEquals(1, location.line());
    }

    @Test
    @DisplayName("copyOf creates independent copy")
    void copyOf() {
      Location original = Location.newFile("test.loop");
      original.nextLine();
      Location copy = Location.copyOf(original);
      assertEquals(2, copy.line());
      original.nextLine();
      assertEquals(3, original.line());
      assertEquals(2, copy.line());
    }
  }

  @Nested
  @DisplayName("Line tracking")
  class LineTracking {

    @Test
    @DisplayName("nextLine increments line number")
    void nextLineIncrements() {
      Location location = Location.newFile("test.loop");
      assertEquals(1, location.line());
      location.nextLine();
      assertEquals(2, location.line());
      location.nextLine();
      assertEquals(3, location.line());
    }
  }

  @Nested
  @DisplayName("Token tracking")
  class TokenTracking {

    @Test
    @DisplayName("nextToken moves left column to right")
    void nextToken() {
      Location location = Location.newFile("test.loop");
      location.extendToken();
      location.extendToken();
      location.nextToken();
      assertEquals(3, location.leftCol());
    }

    @Test
    @DisplayName("extendToken expands right column")
    void extendToken() {
      Location location = Location.newFile("test.loop");
      location.nextToken();
      location.extendToken();
      location.extendToken();
      assertEquals(1, location.leftCol());
      assertEquals(3, location.rightCol());
    }
  }

  @Nested
  @DisplayName("String representation")
  class StringRepresentation {

    @Test
    @DisplayName("toString includes filename and line")
    void toStringFormat() {
      Location location = Location.newFile("test.loop");
      assertTrue(location.toString().startsWith("test.loop:1:"));
      location.nextLine();
      assertTrue(location.toString().startsWith("test.loop:2:"));
    }
  }
}
