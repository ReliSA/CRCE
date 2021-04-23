package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.ArrayList;
import java.util.List;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;



public class VariablesContainer {
    private ArrayList<Variable> vars;


    public Variable get(int index) {
        if (vars.size() <= index) {
            return null;
        }
        return vars.get(index);
    }

    public Variable init(int index) {
        if (index >= vars.size()) {
            Variable last = null;
            final int numIterations = index - vars.size();
            for (int i = 0; i <= numIterations; i++) {
                last = new Variable().setType(VariableType.OTHER);
                this.vars.add(last);
            }
            return last;
        }
        return vars.get(index);
    }

    public void set(int index, Variable var) {
        if (index == vars.size()) {
            vars.add(var);
        } else if (index - vars.size() >= 1) {
            Variable last = null;
            final int numIterations = index - vars.size();
            for (int i = 0; i <= numIterations; i++) {
                last = new Variable().setType(VariableType.OTHER);
                this.vars.add(last);
            }
            vars.set(index, var);
        } else {
            vars.set(index, var).setType(VariableType.SIMPLE);
        }
    }

    /**
     * @param vars
     */
    public VariablesContainer() {
        vars = new ArrayList<>();
        vars.add(new Variable("").setType(VariableType.OTHER));
    }

    public VariablesContainer(List<cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Variable> varStructures,
            String owner) {
        vars = new ArrayList<>();
        if (owner != null) {
            vars.add(new Variable("").setType(VariableType.OTHER).setOwner(owner));
        }
        for (cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Variable varStruct : varStructures) {
            vars.add(new Variable().setDescription(varStruct.getDataType().getBasicType())
                    .setType(VariableType.OTHER));
        }
    }

    public VariablesContainer(List<cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Variable> varStructures) {
        this(varStructures, null);
    }
}
