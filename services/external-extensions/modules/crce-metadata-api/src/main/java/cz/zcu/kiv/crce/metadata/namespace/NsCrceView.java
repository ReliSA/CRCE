package cz.zcu.kiv.crce.metadata.namespace;

import java.util.List;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;

/**
 * TODO drop this and move to identity capability
 *
 * Date: 23.7.17
 *
 * @author Jakub Danek
 */
public class NsCrceView {

    public static final String NAMESPACE__CRCE_VIEW_IDENTITY = "crce.view.identity";

    public static final AttributeType<Long> ATTRIBUTE__SIZE = NsCrceIdentity.ATTRIBUTE__SIZE;

    public static final AttributeType<List<String>> ATTRIBUTE__RESOURCE_IDS = new ListAttributeType("resources");
    public static final AttributeType<List<String>> ATTRIBUTE__RESOURCE_NAMES = new ListAttributeType("resource-names");

    public static final AttributeType<List<String>> ATTRIBUTE__CATEGORIES = NsCrceIdentity.ATTRIBUTE__CATEGORIES;

}
