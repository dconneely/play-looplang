package com.davidconneely.looplang.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class LexerFactory {
  private LexerFactory() {}

  /** Create a new `Stream<String>` from a file `Path`. It MUST be used in try-with-resources. */
  public static Stream<String> lines(final Path path) throws IOException {
    return Files.lines(path);
  }

  /** Create a new `Stream<String>` from an `InputStream`. It MUST be used in try-with-resources. */
  public static Stream<String> lines(final InputStream stream) {
    final BufferedReader r =
        new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));
    return r.lines()
        .onClose(
            () -> {
              try {
                r.close();
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
  }

  public static Lexer newLexer(final Location location, final CodepointInput input) {
    return new DefaultLexer(location, input);
  }

  public static Lexer newLexer(final Location location, final String content) {
    return newLexer(location, content.codePoints());
  }

  public static Lexer newLexer(final Location location, final IntStream codepoints) {
    return newLexer(location, new DefaultCodepointInput(codepoints.iterator()));
  }

  public static Lexer newLexer(final Location location, final Stream<String> lines) {
    // Files.lines() and BufferedReader.lines() strip line separators,
    // so we re-inject '\n' after each line to support the lexer's line-based logic.
    return newLexer(
        location,
        lines.flatMapToInt(line -> IntStream.concat(line.codePoints(), IntStream.of('\n'))));
  }

  public static Lexer newLexer(final Location location, final Reader reader) {
    final BufferedReader br = (reader instanceof BufferedReader b) ? b : new BufferedReader(reader);
    return newLexer(location, br.lines());
  }

  public static Lexer newLexer(final Location location, final InputStream stream) {
    return newLexer(location, lines(stream));
  }
}
