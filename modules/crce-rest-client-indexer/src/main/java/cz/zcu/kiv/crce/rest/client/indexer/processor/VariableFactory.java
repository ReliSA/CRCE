package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Map;
import java.util.Stack;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.WSClient;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ConfigTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;

public class VariableFactory {

    private static final Map<String, Map<String, WSClient>> wsClientData =
            ConfigTools.wsClientData();


    private static boolean isWSClientData(Operation operation) {
        final String owner = operation.getOwner();
        final String methodName = operation.getMethodName();

        if (methodName == null || owner == null) {
            return false;
        }

        return wsClientData.containsKey(owner) && wsClientData.get(owner).containsKey(methodName);
    }

    private static Variable getObjectVariable(Stack<Variable> stack, Operation operation) {
        final String methodName = operation.getMethodName();
        String[] args = MethodTools.getArgsFromSignature(operation.getDescription());
        Variable variable = new Variable();

        if (isWSClientData(operation)) {
            variable.setType(VariableType.WS_CLIENT_DATA);
            
        }
        return null;

    }

    public static Variable getVariable(Stack<Variable> stack, Operation operation) {

        if (operation.getType() == OperationType.CALL
                && MethodTools.getType(operation.getDescription()) == MethodType.INIT) {

        }

        return null;

    }

    public static void main(String[] args) {
        System.out.println("TEST");
    }
}
