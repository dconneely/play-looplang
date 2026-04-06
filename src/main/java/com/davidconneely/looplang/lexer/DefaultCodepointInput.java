package com.davidconneely.looplang.lexer;

import java.util.PrimitiveIterator;

/** A Unicode codepoint input source that reads from a PrimitiveIterator. */
public final class DefaultCodepointInput implements CodepointInput {
  private final PrimitiveIterator.OfInt codepoints;
  private int lookahead;

  public DefaultCodepointInput(final PrimitiveIterator.OfInt codepoints) {
    this.codepoints = codepoints;
    this.lookahead = -1;
  }

  @Override
  public void pushback(final int cp) {
    this.lookahead = cp;
  }

  @Override
  public int next() {
    if (lookahead != -1) {
      final int cp = lookahead;
      lookahead = -1;
      return cp;
    }
    return codepoints.hasNext() ? codepoints.nextInt() : -1;
  }
}
