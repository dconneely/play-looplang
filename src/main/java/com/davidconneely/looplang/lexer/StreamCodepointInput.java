package com.davidconneely.looplang.lexer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** A Unicode codepoint input source that reads from a Stream of lines. */
public final class StreamCodepointInput implements CodepointInput {
  private final PrimitiveIterator.OfInt codepoints;
  private final Deque<Integer> lookahead;

  public StreamCodepointInput(final Stream<String> lines) {
    // Files.lines() and BufferedReader.lines() strip line separators,
    // so we re-inject '\n' after each line to support the lexer's line-based logic.
    this.codepoints =
        lines
            .flatMapToInt(line -> IntStream.concat(line.codePoints(), IntStream.of('\n')))
            .iterator();
    this.lookahead = new ArrayDeque<>();
  }

  @Override
  public void pushback(final int cp) {
    lookahead.push(cp);
  }

  @Override
  public int next() {
    if (!lookahead.isEmpty()) {
      return lookahead.pop();
    }
    return codepoints.hasNext() ? codepoints.nextInt() : -1;
  }
}
