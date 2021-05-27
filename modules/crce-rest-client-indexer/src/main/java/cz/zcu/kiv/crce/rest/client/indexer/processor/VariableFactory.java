package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Stack;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.EndpointDataMiningTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;

public class VariableFactory {



    private static Variable getObjectVariable(Stack<Variable> stack, Operation operation) {
        final String methodName = operation.getMethodName();
        String[] args = MethodTools.getArgsFromSignature(operation.getDescription());
        //EndpointDataMiningTools.getParams(stack,);
        return null;

    }

    public static Variable getVariable(Stack<Variable> stack, Operation operation) {

        if (operation.getType() == OperationType.CALL
                && MethodTools.getType(operation.getDescription()) == MethodType.INIT) {

        }

        return null;

    }
}
