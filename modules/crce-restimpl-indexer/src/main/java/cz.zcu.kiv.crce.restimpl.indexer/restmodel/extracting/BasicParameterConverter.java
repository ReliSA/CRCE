package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.definition.RestApiDefinition;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Variable;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.ParameterCategory;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.RequestParameter;
import cz.zcu.kiv.crce.restimpl.indexer.util.Util;

/**
 * Created by ghessova on 30.03.2018.
 */
public class BasicParameterConverter  {

    private RestApiDefinition restApiDefinition;

    public BasicParameterConverter(RestApiDefinition restApiDefinition) {
        this.restApiDefinition = restApiDefinition;
    }

    public RequestParameter processParameter(Variable variable, boolean determineCategory) {
        RequestParameter parameter = new RequestParameter();
        parameter.setName(getParameterName(variable));
        if (determineCategory) {
            parameter.setCategory(getParameterCategory(variable));
        }
        parameter.setDataType(getParameterDataType(variable));
        parameter.setDefaultValue(getParameterDefaultValue(variable));
        parameter.setOptional(isOptional(variable));
        return parameter;
    }

    /**
     * Returns true if the variable is optional.
     * @param variable
     * @return
     */
    public boolean isOptional(Variable variable) {
        // isOptional == !required
        return !restApiDefinition.getParameterRequirementProcessor().process(variable);
    }

    private ParameterCategory getParameterCategory(Variable variable) {
        String paramCategory = Util.getResultFromAnnotations(restApiDefinition.getEndpointParametersProcessors(), variable.getAnnotations());
        if (paramCategory == null) {
            // todo log
            return null;
        }
        if ("request".equals(paramCategory)) {
            return null;    // category (query or form) is determined by consumes
        }
        return ParameterCategory.valueOf(paramCategory.toUpperCase());
    }

    public String getParameterName(Variable variable) {
        ParameterNameProcessor processor = restApiDefinition.getParameterNameProcessor();
        return processor.process(variable);
    }

    public String getParameterDataType(Variable variable) {
        return variable.getDataType().getBasicType();
    }

    public String getParameterDefaultValue(Variable variable) {
        return Util.getResultFromAnnotations(restApiDefinition.getDefaultParamValuesProcessors(), variable.getAnnotations());
    }
}
