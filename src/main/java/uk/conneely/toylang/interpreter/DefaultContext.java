package uk.conneely.toylang.interpreter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import uk.conneely.toylang.ast.Node;
import uk.conneely.toylang.token.Token;

final class DefaultContext implements Context {
    /**
     * Mutable int value.
     */
    static final class MutInt {
        private int val;

        MutInt(final int val) {
            this.val = val;
        }

        int get() {
            return this.val;
        }

        void set(int val) {
            this.val = val;
        }
    }

    private final Map<String, List<String>> procedureParamDefs;
    private final Map<String, List<Node>> procedureBodies;
    private final Map<String, MutInt> variables;

    DefaultContext() {
        this.procedureParamDefs = new HashMap<>();
        this.procedureBodies = new HashMap<>();
        this.variables = new HashMap<>();
    }

    private DefaultContext(final DefaultContext parent, final String name, final List<Token> paramVals, final int defaultValue) {
        this.procedureParamDefs = parent.procedureParamDefs;
        this.procedureBodies = parent.procedureBodies;
        this.variables = new HashMap<>();
        List<String> paramDefs = parent.procedureParamDefs(name);
        int defCount = paramDefs.size();
        int valCount = paramVals.size();
        if (defCount != valCount) {
            throw new InterpreterException("call: number of parameters expected in procedure definition (" +
                    defCount + ") not equal to number of parameter values passed in procedure call (" + valCount + ")");
        }
        Iterator<String> itDef = paramDefs.iterator();
        Iterator<Token> itVal = paramVals.iterator();
        while (itDef.hasNext() && itVal.hasNext()) {
            String paramDef = itDef.next();
            Token paramVal = itVal.next();

            switch (paramVal.kind()) {
                case IDENT:
                    this.variables.put(paramDef, parent.variables.computeIfAbsent(
                            paramVal.textValue(), _unused -> (new MutInt(defaultValue))));
                    break;
                case INTNUM:
                    this.variables.put(paramDef, new MutInt(paramVal.intValue()));
                    break;
                default:
                    throw new InterpreterException("call: expected an identifier or number");
            }
        }
    }

    @Override
    public void procedure(final String name, final List<String> paramList, final List<Node> body) {
        if (this.procedureParamDefs.containsKey(name) || this.procedureBodies.containsKey(name)) {
            throw new InterpreterException("def: procedure `" + name + "` has already been defined");
        }
        this.procedureParamDefs.put(name, paramList);
        this.procedureBodies.put(name, body);
    }

    @Override
    public List<String> procedureParamDefs(final String name) {
        if (!this.procedureParamDefs.containsKey(name)) {
            throw new InterpreterException("call: procedure `" + name + "` has not been defined");
        }
        return this.procedureParamDefs.get(name);
    }

    @Override
    public List<Node> procedureBody(final String name) {
        if (!this.procedureBodies.containsKey(name)) {
            throw new InterpreterException("call: procedure `" + name + "` has not been defined");
        }
        return this.procedureBodies.get(name);
    }

    @Override
    public void variable(final String name, final int newValue) {
        if (this.variables.containsKey(name)) {
            final MutInt value = this.variables.get(name);
            value.set(newValue);
        } else {
            final MutInt value = new MutInt(newValue);
            this.variables.put(name, value);
        }
    }

    @Override
    public int variable(final String name) {
        if (!this.variables.containsKey(name)) {
            throw new InterpreterException("variable `" + name + "` has not been defined");
        }
        final MutInt value = this.variables.get(name);
        return value.get();
    }

    @Override
    public Context procedureCall(String name, final List<Token> paramValues, final int defaultValue) {
        return new DefaultContext(this, name, paramValues, defaultValue);
    }
}
