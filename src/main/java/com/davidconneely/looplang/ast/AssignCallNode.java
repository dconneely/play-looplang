package com.davidconneely.looplang.ast;

import com.davidconneely.looplang.interpreter.Context;
import com.davidconneely.looplang.interpreter.Interpreter;
import com.davidconneely.looplang.interpreter.InterpreterException;
import com.davidconneely.looplang.interpreter.InterpreterFactory;
import com.davidconneely.looplang.lexer.Lexer;
import com.davidconneely.looplang.parser.ParserException;
import com.davidconneely.looplang.token.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.davidconneely.looplang.ast.NodeUtils.nextTokenWithKind;
import static com.davidconneely.looplang.ast.NodeUtils.throwUnexpectedParserException;
import static com.davidconneely.looplang.token.Token.Kind.*;

final class AssignCallNode implements Node {
    private String variable; // variable name on left of `:=` sign
    private String program;  // called program name to right of `:=` sign
    private List<String> args;  // variable names of the args to the call
    private final Set<String> programs;  // fully-defined programs

    AssignCallNode(final Set<String> programs) {
        this.programs = programs;
    }

    @Override
    public void parse(final Lexer lexer) throws IOException {
        variable = nextTokenWithKind(lexer, IDENTIFIER, "as lvalue variable name in call assignment").textValue();
        nextTokenWithKind(lexer, ASSIGN, "after lvalue in call assignment");
        program = nextTokenWithKind(lexer, IDENTIFIER, "as program name in call").textValue();
        checkProgramDefined();
        nextTokenWithKind(lexer, LPAREN, "before args list in call");
        args = DefinitionNode.nextTokensAsCSVNames(lexer, "in args list in call");
    }

    private void checkProgramDefined() {
        if (!programs.contains(program)) {
            throw new ParserException("program `" + program + "` is not fully-defined before call to it");
        }
    }

    @Override
    public void interpret(final Context context) {
        if (variable == null || program == null || args == null) {
            throw new InterpreterException("uninitialized call assignment");
        }
        final Context subcontext = context.getProgramContextOrThrow(program, args);
        if (subcontext.getVariable("X0").isEmpty()) {
            subcontext.setVariable("X0", 0);
        }
        final List<Node> body = context.getProgramBody(program);
        final Interpreter interpreter = InterpreterFactory.newInterpreter(subcontext);
        for (Node node : body) {
            interpreter.interpret(node);
        }
        final int x0 = subcontext.getVariable("X0").orElse(0);
        context.setVariable(variable, x0);
    }

    @Override
    public String toString() {
        if (variable == null || program == null || args == null) {
            return "<uninitialized call assignment>";
        }
        return variable.toLowerCase(Locale.ROOT) + " := " + program.toUpperCase(Locale.ROOT) + '(' +
                args.stream().map(arg -> arg.toLowerCase(Locale.ROOT)).collect(Collectors.joining(", ")) + ')';
    }
}