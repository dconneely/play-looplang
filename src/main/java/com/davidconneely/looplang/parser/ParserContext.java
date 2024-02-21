package com.davidconneely.looplang.parser;

import java.util.HashSet;
import java.util.Set;

public final class ParserContext {
    private final Set<String> definedPrograms;

    ParserContext() {
        this.definedPrograms = new HashSet<>();
    }

    public void addDefinedProgram(final String name) {
        definedPrograms.add(name);
    }

    public void checkProgramIsDefined(final String name) {
        if (!definedPrograms.contains(name)) {
            throw new ParserException("program `" + name + "` is not fully-defined before call to it");
        }
    }
}
