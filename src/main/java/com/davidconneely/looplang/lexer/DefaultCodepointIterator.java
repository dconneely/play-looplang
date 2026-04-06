package com.davidconneely.looplang.lexer;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

/** A default implementation of CodepointIterator that wraps a primitive iterator. */
final class DefaultCodepointIterator implements CodepointIterator {
  private final PrimitiveIterator.OfInt codepoints;
  private final Runnable onClose;
  private int lookahead = -1;

  DefaultCodepointIterator(final PrimitiveIterator.OfInt codepoints, final Runnable onClose) {
    this.codepoints = codepoints;
    this.onClose = onClose;
  }

  @Override
  public void pushback(final int cp) {
    this.lookahead = cp;
  }

  @Override
  public boolean hasNext() {
    return (lookahead != -1) || codepoints.hasNext();
  }

  @Override
  public int nextCodepoint() {
    if (lookahead != -1) {
      final int cp = lookahead;
      lookahead = -1;
      return cp;
    }
    if (!codepoints.hasNext()) {
      throw new NoSuchElementException();
    }
    return codepoints.nextInt();
  }

  @Override
  public void close() {
    if (onClose != null) {
      onClose.run();
    }
  }
}
