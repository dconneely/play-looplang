package com.davidconneely.looplang;

import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.token.LocatedToken;
import com.davidconneely.looplang.token.Token;

public class LocatedException extends RuntimeException {
    private final Location location;

    public LocatedException(final String message) {
        super(message);
        this.location = null;
    }

    public LocatedException(final String message, final Throwable cause) {
        super(message, cause);
        this.location = null;
    }

    public LocatedException(final String message, final Location location) {
        super(message);
        this.location = Location.copyOf(location);
    }

    public LocatedException(final String message, final Throwable cause, final Location location) {
        super(message, cause);
        this.location = Location.copyOf(location);
    }

    public LocatedException(final String message, final Token token) {
        super(message);
        if (token instanceof LocatedToken locatedToken) {
            this.location = Location.copyOf(locatedToken.location());
        } else {
            this.location = null;
        }
   }

    public LocatedException(final String message, final Throwable cause, final Token token) {
        super(message, cause);
        if (token instanceof LocatedToken locatedToken) {
            this.location = Location.copyOf(locatedToken.location());
        } else {
            this.location = null;
        }
    }

    public Location location() {
        return location;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (location != null) {
            sb.append(location);
        }
        sb.append(getClass().getSimpleName());
        final String message = getLocalizedMessage();
        if (message != null && !message.isBlank()) {
            sb.append(": ").append(message);
        }
        return sb.toString();
    }
}
