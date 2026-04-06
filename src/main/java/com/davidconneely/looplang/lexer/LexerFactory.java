package com.davidconneely.looplang.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class LexerFactory {
  private LexerFactory() {}

  /** Create a new CodepointIterator from a file `Path`. It MUST be used in try-with-resources. */
  public static CodepointIterator newCodepointIterator(final Path path) throws IOException {
    final Stream<String> lines = Files.lines(path);
    return newCodepointIterator(lines);
  }

  /**
   * Create a new CodepointIterator from an `InputStream`. It MUST be used in try-with-resources.
   */
  public static CodepointIterator newCodepointIterator(final InputStream stream) {
    final Reader r = new InputStreamReader(Objects.requireNonNull(stream));
    final BufferedReader br = (r instanceof BufferedReader b) ? b : new BufferedReader(r);
    return newCodepointIterator(br.lines());
  }

  /**
   * Create a new CodepointIterator from a `Stream<String>`. It MUST be used in try-with-resources.
   */
  public static CodepointIterator newCodepointIterator(final Stream<String> lines) {
    // Files.lines() and BufferedReader.lines() strip line separators,
    // so we re-inject '\n' after each line to support the lexer's line-based logic.
    final IntStream cpStream =
        lines.flatMapToInt(line -> IntStream.concat(line.codePoints(), IntStream.of('\n')));
    return new DefaultCodepointIterator(cpStream.iterator(), cpStream::close);
  }

  public static Lexer newLexer(final Location location, final CodepointIterator input) {
    return new DefaultLexer(location, input);
  }

  public static Lexer newLexer(final Location location, final String content) {
    final IntStream stream = content.codePoints();
    return newLexer(location, new DefaultCodepointIterator(stream.iterator(), stream::close));
  }

  public static Lexer newLexer(final Location location, final Reader reader) {
    final BufferedReader br = (reader instanceof BufferedReader b) ? b : new BufferedReader(reader);
    return newLexer(location, newCodepointIterator(br.lines()));
  }

  public static Lexer newLexer(final Location location, final InputStream stream) {
    return newLexer(location, newCodepointIterator(stream));
  }
}
