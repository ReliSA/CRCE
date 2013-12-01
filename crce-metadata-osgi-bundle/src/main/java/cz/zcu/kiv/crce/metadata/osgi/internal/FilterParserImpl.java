package cz.zcu.kiv.crce.metadata.osgi.internal;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.osgi.util.FilterParser;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = FilterParser.class)
public class FilterParserImpl implements FilterParser {

    private static final char OPERATOR_AND = '&';
    private static final char OPERATOR_OR = '|';
    private static final char OPERATOR_NOT = '!';

    private static final char OPEN = '(';
    private static final char CLOSE = ')';

    @ServiceDependency private volatile ResourceFactory resourceFactory;

    @Override
    public Requirement parse(String filter, String namespace) throws InvalidSyntaxException {
        Requirement requirement = resourceFactory.createRequirement(namespace);

        filter = filter.trim();
        if (filter.isEmpty()) {
            return requirement;
        }

        int level;
        try {
            level = parse(filter, 0, requirement);
        } catch (IllegalArgumentException e) {
            throw new InvalidSyntaxException(e.getMessage(), filter);
        }

        if (level > 0) {
            throw new InvalidSyntaxException("Missing closing parenthesis", filter);
        } else if (level < 0) {
            throw new InvalidSyntaxException("Superfluous closing parenthesis", filter);
        }

        return requirement;
    }

    @SuppressWarnings("fallthrough")
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SF_SWITCH_FALLTHROUGH", justification = "Fallthrough is intended.")
    private int parse(String filter, int level, Requirement requirement) {
        switch (filter.charAt(0)) {
            case OPEN:
                return parse(filter.substring(1), level + 1, requirement);

            case CLOSE:
                if (filter.length() > 1) {
                    parse(filter.substring(1), level - 1, requirement);
                }
                return level - 1;

            case OPERATOR_OR:
                requirement.setDirective("operator", "or"); // TODO constants
                // fallthrough intended
            case OPERATOR_AND:
                return parse(filter.substring(1), level, requirement) - 1;

            case OPERATOR_NOT:
            default:
                int index = filter.indexOf(CLOSE);
                if (index > 0) {
                    parse(filter.substring(0, index), requirement);
                    return parse(filter.substring(index), level, requirement);
                }

        }

        throw new UnsupportedOperationException("Not supported yet");
    }

    private void parse(String string, Requirement requirement) {
        Operator operator;
        String[] split;
        op: {
            // two characters operator
            split = string.split(">=");
            if (split.length == 2) {
                operator = Operator.GREATER_EQUAL;
                break op;
            }
            split = string.split("<=");
            if (split.length == 2) {
                operator = Operator.LESS_EQUAL;
                break op;
            }
            split = string.split("<\\*");
            if (split.length == 2) {
                operator = Operator.SUBSET;
                break op;
            }
            split = string.split("\\*>");
            if (split.length == 2) {
                operator = Operator.SUPERSET;
                break op;
            }
            split = string.split("~=");
            if (split.length == 2) {
                operator = Operator.APPROX;
                break op;
            }
            // one character operator
            split = string.split("=");
            if (split.length == 2) {
                operator = Operator.EQUAL;
                break op;
            }
            split = string.split("<");
            if (split.length == 2) {
                operator = Operator.LESS;
                break op;
            }
            split = string.split(">");
            if (split.length == 2) {
                operator = Operator.GREATER;
                break op;
            }
            throw new IllegalArgumentException("Missing or superfluous operator: " + string);
    // TODO   NOT_EQUAL("not-equal"),
        }

        String name = split[0];
        String value = split[1];

        if ("*".equals(value)) {
            requirement.addAttribute(name, String.class, value, Operator.PRESENT);
        } else if (NsOsgiPackage.NAMESPACE__OSGI_PACKAGE.equals(requirement.getNamespace())) {
            switch (name) {
                case "package":
                case "osgi.wiring.package":
                    requirement.addAttribute(NsOsgiPackage.ATTRIBUTE__NAME, value, operator);
                    break;
                case "version":
                    requirement.addAttribute(NsOsgiPackage.ATTRIBUTE__VERSION, new Version(value), operator);
                    break;
                default:
                    requirement.addAttribute(name, String.class, value, operator);
                    break;
            }
        } else {
            requirement.addAttribute(name, String.class, value, operator);
        }
    }
}
