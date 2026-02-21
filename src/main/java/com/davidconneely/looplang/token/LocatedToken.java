package com.davidconneely.looplang.token;

import com.davidconneely.looplang.lexer.LexerException;
import com.davidconneely.looplang.lexer.Location;

public record LocatedToken(Token token, Location location) implements Token {
  @Override
  public Token.Kind kind() {
    return token.kind();
  }

  @Override
  public String value() {
    return token.value();
  }

  @Override
  public int valueInt() {
    return token.valueInt();
  }

  /** Prevent re-location. */
  @Override
  public LocatedToken at(final Location location) {
    throw new LexerException("attempt to relocate an already-located token to " + location, this);
  }

  @Override
  public String toString() {
    // TODO: for now don't include the location information in the toString() - does this make sense
    // long-term?
    return token.toString();
  }
}
