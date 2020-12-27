package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.DirectiveProvider;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.type.Version;


@ParametersAreNonnullByDefault
public class MetadataDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(MetadataDeserializer.class);

    private final MetadataFactory metadataFactory;

    public MetadataDeserializer(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    Resource deserializeResource(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeResource((JsonNode) jp.getCodec().readTree(jp));
    }

    Requirement deserializeRequirement(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeRequirement((JsonNode) jp.getCodec().readTree(jp), null, null, null);
    }

    Capability deserializeCapability(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeCapability((JsonNode) jp.getCodec().readTree(jp), null, null);
    }

    Property deserializeProperty(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return deserializeProperty((JsonNode) jp.getCodec().readTree(jp), null);
    }

    private Resource deserializeResource(JsonNode root) throws IOException, JsonProcessingException {
        JsonNode id = root.findValue(Constants.RESOURCE__ID);
        Resource resource = id == null ? metadataFactory.createResource() : metadataFactory.createResource(id.asText());

        Iterator<Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> node = fields.next();
            switch (node.getKey()) {
//                case Constants.RESOURCE__REPOSITORY:
//                    deserializeRepository(resource, node.getValue());
//                    continue;

                case Constants.RESOURCE__CAPABILITIES:
                    deserializeCapabilities(resource, null, node.getValue());
                    continue;

                case Constants.RESOURCE__REQUIREMENTS:
                    deserializeRequirements(resource, null, null, node.getValue());
                    continue;

                case Constants.RESOURCE__PROPERTIES:
                    deserializeProperties(resource, node.getValue());
                    continue;

                default:
                    logger.trace("Uknown field: {}, value: {}", node.getKey(), node.getValue());
            }
        }

        return resource;
    }

//    private void deserializeRepository(Resource resource, JsonNode root) throws IOException {
//        Iterator<Entry<String, JsonNode>> fields = root.fields();
//        while (fields.hasNext()) {
//            Entry<String, JsonNode> node = fields.next();
//            switch (node.getKey()) {
//                case Constants.REPOSITORY__URI:
//                    Repository repository;
//                    try {
//                        repository = metadataFactory.createRepository(new URI(node.getValue().asText()));
//                    } catch (URISyntaxException ex) {
//                        throw new IOException("Invalid URI: " + node.getValue().asText(), ex);
//                    }
//                    resource.setRepository(repository);
//                    return;
//
//                default:
//            }
//        }
//    }

    @SuppressWarnings("unchecked")
    private void deserializeCapabilities(@CheckForNull Resource resource, @CheckForNull Capability parent, JsonNode root) {
        switch (root.getNodeType()) {
            case NULL:
                return;

            case ARRAY: {
                Iterator<JsonNode> capabilityNodes = root.elements();
                while (capabilityNodes.hasNext()) {
                    deserializeCapability(capabilityNodes.next(), resource, parent);
                }
                break;
            }

            default:
                logger.warn("Array of capabilities was expected: {}", root);
        }

    }

    private Capability deserializeCapability(JsonNode capabilityNode, @CheckForNull Resource resource, @CheckForNull Capability parent)
            throws IllegalArgumentException {

        JsonNode namespace = capabilityNode.findValue(Constants.CAPABILITY__NAMESPACE);
        if (namespace == null || !namespace.isTextual()) {
            throw new IllegalArgumentException("Textual namespace is mandatory for capability: " + capabilityNode);
        }
        JsonNode id = capabilityNode.findValue(Constants.CAPABILITY__ID);

        final Capability capability =
                id == null
                ? metadataFactory.createCapability(namespace.asText())
                : metadataFactory.createCapability(namespace.asText(), id.asText());

        if (resource != null) {
            resource.addCapability(capability);
        }
        if (parent == null) {
            if (resource != null) {
                resource.addRootCapability(capability);
            }
        } else {
            parent.addChild(capability);
        }

        Iterator<Entry<String, JsonNode>> fields = capabilityNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> node = fields.next();
            switch (node.getKey()) {
                case Constants.CAPABILITY__ATTRIBUTES:
                    deserializeAttributes(
                            new AttributeProviderCallback() {

                                @Override
                                @SuppressWarnings("unchecked")
                                public void addAttribute(AttributeType type, Object value, Operator operator) {
                                    capability.setAttribute(type, value, operator);
                                }
                            },
                            node.getValue()
                    );
                    continue;

                case Constants.CAPABILITY__DIRECTIVES:
                    deserializeDirectives(capability, node.getValue());
                    continue;

                case Constants.CAPABILITY__REQUIREMENTS:
                    deserializeRequirements(null, capability, null, node.getValue());
                    continue;

                case Constants.CAPABILITY__PROPERTIES:
                    deserializeProperties(capability, node.getValue());
                    continue;

                case Constants.CAPABILITY__CHILDREN:
                    deserializeCapabilities(resource, capability, node.getValue());
                    continue;

                case Constants.CAPABILITY__ID:
                case Constants.CAPABILITY__NAMESPACE:
                    continue;

                default:
                    logger.debug("Ignoring unknow capability subnode, key: {}, value: {}", node.getKey(), node.getValue());
            }
        }
        return capability;
    }

    private interface AttributeProviderCallback {

        <T> void addAttribute(AttributeType<T> type, T value, Operator operator);
    }

    @SuppressWarnings("unchecked")
    private void deserializeRequirements(@CheckForNull Resource resource, @CheckForNull Capability capability, @CheckForNull Requirement parent, JsonNode root) {
        switch (root.getNodeType()) {
            case NULL:
                return;

            case ARRAY: {
                Iterator<JsonNode> requirementNodes = root.elements();
                while (requirementNodes.hasNext()) {
                    JsonNode requirementNode = requirementNodes.next();

                    deserializeRequirement(requirementNode, resource, capability, parent);
                }
                break;
            }

            default:
                logger.warn("Array of capabilities was expected: {}", root);
        }
    }

    private Requirement deserializeRequirement(JsonNode requirementNode, @CheckForNull Resource resource, @CheckForNull Capability capability, @CheckForNull Requirement parent)
            throws IllegalArgumentException {

        JsonNode namespace = requirementNode.findValue(Constants.REQUIREMENT__NAMESPACE);
        if (namespace == null || !namespace.isTextual()) {
            throw new IllegalArgumentException("Textual namespace is mandatory for requirement: " + requirementNode);
        }
        JsonNode id = requirementNode.findValue(Constants.REQUIREMENT__ID);

        final Requirement requirement =
                id == null
                ? metadataFactory.createRequirement(namespace.asText())
                : metadataFactory.createRequirement(namespace.asText(), id.asText());

        if (parent == null) {
            if (resource != null) {
                resource.addRequirement(requirement);
            }
            if (capability != null) {
                capability.addRequirement(requirement);
            }
        } else {
            parent.addChild(requirement);
        }
        Iterator<Entry<String, JsonNode>> fields = requirementNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> node = fields.next();
            switch (node.getKey()) {
                case Constants.REQUIREMENT__ATTRIBUTES:
                    deserializeAttributes(
                            new AttributeProviderCallback() {

                                @Override
                                @SuppressWarnings("unchecked")
                                public void addAttribute(AttributeType type, Object value, Operator operator) {
                                    requirement.addAttribute(type, value, operator);
                                }
                            },
                            node.getValue()
                    );
                    continue;

                case Constants.REQUIREMENT__DIRECTIVES:
                    deserializeDirectives(requirement, node.getValue());
                    continue;

                case Constants.REQUIREMENT__CHILDREN:
                    deserializeRequirements(resource, capability, requirement, node.getValue());
                    continue;

                case Constants.REQUIREMENT__ID:
                case Constants.REQUIREMENT__NAMESPACE:
                    continue;

                default:
                    logger.debug("Ignoring unknow requirement subnode, key: {}, value: {}", node.getKey(), node.getValue());
            }
        }
        return requirement;
    }

    @SuppressWarnings("unchecked")
    private void deserializeProperties(@CheckForNull PropertyProvider parent, JsonNode root) {
        switch (root.getNodeType()) {
            case NULL:
                return;

            case ARRAY: {
                Iterator<JsonNode> propertyNodes = root.elements();
                while (propertyNodes.hasNext()) {
                    JsonNode propertyNode = propertyNodes.next();

                    deserializeProperty(propertyNode, parent);
                }
                break;
            }

            default:
                logger.warn("Array of capabilities was expected: {}", root);
        }
    }

    private Property deserializeProperty(JsonNode propertyNode, @CheckForNull PropertyProvider parent) throws IllegalArgumentException {
        JsonNode namespace = propertyNode.findValue(Constants.PROPERTY__NAMESPACE);
        if (namespace == null || !namespace.isTextual()) {
            throw new IllegalArgumentException("Textual namespace is mandatory for requirement: " + propertyNode);
        }
        JsonNode id = propertyNode.findValue(Constants.PROPERTY__ID);

        final Property property;
        if (id == null) {
            property = metadataFactory.createProperty(namespace.asText());
        } else {
            property = metadataFactory.createProperty(namespace.asText(), id.asText());
        }

        if (parent != null) {
            parent.addProperty(property);
        }

        Iterator<Entry<String, JsonNode>> fields = propertyNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> node = fields.next();
            switch (node.getKey()) {
                case Constants.PROPERTY__ATTRIBUTES:
                    deserializeAttributes(
                            new AttributeProviderCallback() {

                                @Override
                                @SuppressWarnings("unchecked")
                                public void addAttribute(AttributeType type, Object value, Operator operator) {
                                    property.setAttribute(type, value, operator);
                                }
                            },
                            node.getValue()
                    );
                    continue;

                case Constants.PROPERTY__ID:
                case Constants.PROPERTY__NAMESPACE:
                    continue;

                default:
                    logger.debug("Ignoring unknow property subnode, key: {}, value: {}", node.getKey(), node.getValue());
            }
        }
        return property;
    }

    private <T> void deserializeAttributes(AttributeProviderCallback callback, JsonNode root) {
        switch (root.getNodeType()) {
            case NULL:
                return;

            case ARRAY: {
                Iterator<JsonNode> attributeNodes = root.elements();
                while (attributeNodes.hasNext()) {
                    JsonNode attributeNode = attributeNodes.next();

                    JsonNode nameNode = attributeNode.findValue(Constants.ATTRIBUTE__NAME);
                    JsonNode typeNode = attributeNode.findValue(Constants.ATTRIBUTE__TYPE);
                    JsonNode valueNode = attributeNode.findValue(Constants.ATTRIBUTE__VALUE);
                    JsonNode operatorNode = attributeNode.findValue(Constants.ATTRIBUTE__OPERATOR);

                    if (nameNode == null || !nameNode.isTextual()) {
                        throw new IllegalArgumentException("Textual attribute name is mandatory: " + attributeNode);
                    }
                    String name = nameNode.asText();

                    if (typeNode != null && !typeNode.isTextual()) {
                        throw new IllegalArgumentException("Attribute type must be textual: " + attributeNode);
                    }
                    String type = typeNode == null ? String.class.getSimpleName() : typeNode.asText();

                    if (operatorNode != null && !operatorNode.isTextual()) {
                        throw new IllegalArgumentException("Attribute operator must be textual: " + attributeNode);
                    }
                    Operator operator = operatorNode == null ? Operator.EQUAL : Operator.fromValue(operatorNode.asText());

                    if (valueNode == null) {
                        throw new IllegalArgumentException("Attribute value is mandatory: " + attributeNode);
                    }

                    switch (type) {
                        default:
                        case "String":
                        case "java.lang.String":
                            callback.addAttribute(new SimpleAttributeType<>(name, String.class), valueNode.asText(), operator);
                            continue;

                        case "Long":
                        case "java.lang.Long":
                            callback.addAttribute(new SimpleAttributeType<>(name, Long.class), valueNode.asLong(), operator);
                            continue;

                        case "Double":
                        case "java.lang.Double":
                            callback.addAttribute(new SimpleAttributeType<>(name, Double.class), valueNode.asDouble(), operator);
                            continue;

                        case "Version":
                        case "cz.zcu.kiv.crce.metadata.type.Version":
                            callback.addAttribute(new SimpleAttributeType<>(name, Version.class), deserializeVersion(valueNode), operator);
                            continue;

                        case "List":
                        case "java.util.List":
                            callback.addAttribute(new ListAttributeType(name), deserializeList(valueNode), operator);
                            continue;

                        case "Boolean":
                            callback.addAttribute(new SimpleAttributeType<>(name, Boolean.class), valueNode.asBoolean(), operator);
                            continue;

                        case "URI":
                        case "java.net.URI":
                            try {
                                callback.addAttribute(new SimpleAttributeType<>(name, URI.class), new URI(valueNode.asText()), operator);
                            } catch (URISyntaxException ex) {
                                throw new IllegalArgumentException("Invalid URI: " + valueNode.asText(), ex);
                            }
                    }

                }
                break;
            }

            default:
                logger.warn("Array of attributes was expected: {}", root);
        }
    }

    private Version deserializeVersion(JsonNode root) {
        if (root.isTextual()) {
            return new Version(root.asText());
        } else if (root.isObject()) {
            JsonNode qualifierNode = root.findValue(Constants.ATTRIBUTE__VERSION_QUALIFIER);
                return new Version(
                    root.findValue(Constants.ATTRIBUTE__VERSION_MAJOR).asInt(),
                    root.findValue(Constants.ATTRIBUTE__VERSION_MINOR).asInt(),
                    root.findValue(Constants.ATTRIBUTE__VERSION_MICRO).asInt(),
                    qualifierNode == null ? null : qualifierNode.asText());
        }
        throw new IllegalArgumentException("Can't parse Version value: " + root);
    }

    private List<String> deserializeList(JsonNode root) {
        if (root.isArray()) {
            List<String> result = new ArrayList<>(root.size());
            Iterator<JsonNode> iterator = root.iterator();
            while (iterator.hasNext()) {
                result.add(iterator.next().asText());
            }
            return result;
        } else if (!root.isObject()) {
            if (root.isTextual()) {
                return Arrays.asList(root.asText().split(","));
            }
            return Collections.singletonList(root.asText());
        }
        throw new IllegalArgumentException("Can't parse List value: " + root);
    }


    private void deserializeDirectives(DirectiveProvider directiveProvider, JsonNode root) {
        if (root.isObject()) {
            Iterator<Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Entry<String, JsonNode> directive = fields.next();
                JsonNode value = directive.getValue();
                if (!value.isObject() && !value.isArray()) {
                    directiveProvider.setDirective(directive.getKey(), value.asText());
                }
            }
        }
    }

}
