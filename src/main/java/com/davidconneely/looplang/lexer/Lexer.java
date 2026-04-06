package com.davidconneely.looplang.lexer;

import com.davidconneely.looplang.token.Token;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** A lexical analyzer that produces a stream of tokens from a source. */
public interface Lexer extends Iterator<Token>, AutoCloseable {
  /**
   * Check if there are more tokens available (other than EOF).
   *
   * @return true if there is a token available, false at EOF.
   */
  @Override
  boolean hasNext();

  /**
   * Get the next token.
   *
   * @return the next token, or an EOF token if no more tokens are available.
   * @throws java.io.UncheckedIOException if an I/O error occurs.
   */
  @Override
  Token next();

  /**
   * Push back a token so it will be returned by the next call to {@link #next()}.
   *
   * @param token the token to push back.
   */
  void pushback(Token token);

  @Override
  void close();

  /**
   * Create a stream of tokens from this lexer (including the EOF token).
   *
   * @return a stream of tokens.
   */
  default Stream<Token> tokens() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            this, Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE),
        false);
  }
}
