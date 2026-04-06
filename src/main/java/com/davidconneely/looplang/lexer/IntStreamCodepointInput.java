package com.davidconneely.looplang.lexer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PrimitiveIterator;

/** A Unicode codepoint input source that reads from a PrimitiveIterator. */
public final class IntStreamCodepointInput implements CodepointInput {
  private final PrimitiveIterator.OfInt codepoints;
  private final Deque<Integer> lookahead;

  public IntStreamCodepointInput(final PrimitiveIterator.OfInt codepoints) {
    this.codepoints = codepoints;
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
