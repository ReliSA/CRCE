package cz.zcu.kiv.crce.metadata.osgi.internal;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.osgi.util.FilterParser;

/**
 * Implementation is based on:
 * http://www.ietf.org/rfc/rfc1960.txt
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

    @ServiceDependency private volatile MetadataFactory metadataFactory;

    @Override
    public Requirement parse(String filter, String namespace) throws InvalidSyntaxException {
        Requirement requirement = metadataFactory.createRequirement(namespace);

        filter = filter.trim();
        if (filter.isEmpty()) {
            return requirement;
        }

        parseFilter(filter, requirement, 0, false);

        return requirement;
    }

    private void parseFilter(String filter, Requirement requirement, int level, boolean not) throws InvalidSyntaxException {
        if (OPEN != filter.charAt(0)) {
            throw new InvalidSyntaxException("Missing opening parenthesis: " + filter, filter);
        }
        if (CLOSE != filter.charAt(filter.length() - 1)) {
            throw new InvalidSyntaxException("Missing closing parenthesis: " + filter, filter);
        }
        parseFilterComp(filter.substring(1, filter.length() - 1), requirement, level, not);
    }

    @SuppressWarnings("unchecked")
    private void parseFilterComp(String filterComp, Requirement requirement, int level, boolean not) throws InvalidSyntaxException {
        switch (filterComp.charAt(0)) {
            case OPERATOR_AND:
                parseFilterList(filterComp.substring(1), nestRequirement(requirement, level), level + 1);
                return;

            case OPERATOR_OR: {
                Requirement nested = nestRequirement(requirement, level);
                nested.setDirective("operator", "or"); // TODO constants
                parseFilterList(filterComp.substring(1), nested, level + 1);
                return;
            }

            case OPERATOR_NOT: {
                parseFilter(filterComp.substring(1), requirement, level + 1, !not);
//                Requirement nested = nestRequirement(requirement, level);
//                nested.setDirective("operator", "not"); // TODO constants
//                parseFilter(filterComp.substring(1), nested, level + 1, false);
                return;
            }

            default:
                parseItem(filterComp, requirement, not);
        }
    }

    private Requirement nestRequirement(Requirement parent, int level) {
        if (level > 0) {
            Requirement child = metadataFactory.createRequirement(parent.getNamespace());
            parent.addChild(child);
            child.setParent(parent);
            return child;
        }
        return parent;
    }

    private void parseFilterList(String filterList, Requirement requirement, int level) throws InvalidSyntaxException {
        int begin = 0;
        int depth = 0;
        for (int i = 0; i < filterList.length(); i++) {
            if (filterList.charAt(i) == OPEN) {
                depth++;
                if (depth == 1) {
                    begin = i;
                }
            } else  if (filterList.charAt(i) == CLOSE) {
                depth--;
                if (depth == 0) {
                    parseFilter(filterList.substring(begin, i + 1), requirement, level, false);
                }
            }
        }
        if (depth < 0) {
            throw new InvalidSyntaxException("Superfluous closing parenthesis: " + filterList, filterList);
        }
        if (depth > 0) {
            throw new InvalidSyntaxException("Missing closing parenthesis: " + filterList, filterList);
        }
    }

    private void parseItem(String item, Requirement requirement, boolean not) {
        Operator operator;
        String[] split;
        op: {
            // two characters operator
            split = item.split(">=");
            if (split.length == 2) {
                operator = not ? Operator.LESS : Operator.GREATER_EQUAL;
                break op;
            }
            split = item.split("<=");
            if (split.length == 2) {
                operator = not ? Operator.GREATER : Operator.LESS_EQUAL;
                break op;
            }
            split = item.split("<\\*");
            if (split.length == 2) {
                if (not) {
                    throw new IllegalArgumentException("Negation of SUBSET operator is not supported yet.");
                }
                operator = Operator.SUBSET;
                break op;
            }
            split = item.split("\\*>");
            if (split.length == 2) {
                if (not) {
                    throw new IllegalArgumentException("Negation of SUPERSET operator is not supported yet.");
                }
                operator = Operator.SUPERSET;
                break op;
            }
            split = item.split("~=");
            if (split.length == 2) {
                if (not) {
                    throw new IllegalArgumentException("Negation of APPROX operator is not supported yet.");
                }
                operator = Operator.APPROX;
                break op;
            }
            // one character operator
            split = item.split("=");
            if (split.length == 2) {
                operator = not ? Operator.NOT_EQUAL : Operator.EQUAL;
                break op;
            }
            split = item.split("<");
            if (split.length == 2) {
                operator = not ? Operator.GREATER_EQUAL : Operator.LESS;
                break op;
            }
            split = item.split(">");
            if (split.length == 2) {
                operator = not ? Operator.LESS_EQUAL : Operator.GREATER;
                break op;
            }
            throw new IllegalArgumentException("Missing or superfluous operator: " + item);
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
