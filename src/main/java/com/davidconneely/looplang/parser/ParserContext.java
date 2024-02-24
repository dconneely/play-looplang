package com.davidconneely.looplang.parser;

import com.davidconneely.looplang.lexer.Location;
import com.davidconneely.looplang.token.Token;

import java.util.HashSet;
import java.util.Set;

public final class ParserContext {
    private final Location location;
    private final Set<String> definedPrograms;

    ParserContext(final Location location) {
        this.definedPrograms = new HashSet<>();
        this.location = location;
    }

    public Location location() {
        return location;
    }

    public void addDefinedProgram(final String name) {
        definedPrograms.add(name);
    }

    public void checkProgramIsDefined(final String name, final Token token) {
        if (!definedPrograms.contains(name)) {
            throw new ParserException("program `" + name + "` is not fully-defined before call to it", token);
        }
    }
}
