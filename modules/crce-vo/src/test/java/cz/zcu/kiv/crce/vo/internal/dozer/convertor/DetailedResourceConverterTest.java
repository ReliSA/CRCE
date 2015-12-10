package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.vo.model.metadata.AttributeVO;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericCapabilityVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericRequirementVO;
import cz.zcu.kiv.crce.vo.model.metadata.PropertyVO;

/**
 * Test class of {@link DetailedResourceConverter}.
 *
 * Date: 5.6.15
 *
 * @author Jakub Danek
 */
public class DetailedResourceConverterTest {

    private static final String CUSTOM_CAPABILITY_NS = "custom.namespace";
    private static final String CUSTOM_ATTR_NAME = "custom.attribute";
    private static final String CUSTOM_ATTR_VALUE = "custom.attr.value";
    private static final String CUSTOM_PROPERTY_NS = "custom.prop.ns";
    private static final String CUSTOM_REQUIREMENT_NS = "custom.req.ns";
    private static final String CUSTOM_ID = "id";
    private static final String CMP_NAME = "cz.zcu.kiv.vo.test.Component";

    private DetailedResourceConverter convertor;

    /*
        ##### MOCKS #####
     */
    @Mock
    private MetadataService metadataService;
    @Mock
    private Resource resource;

    /*
    TEST DATA
     */
    private Version version;
    private Long size;
    private List<String> categories;


    @Before
    public void setUp() throws Exception {
        initMocks();
        initDozer();
    }

    private void initMocks() {
        MockitoAnnotations.initMocks(this);
        reset(metadataService, resource);
    }


    private void initDozer() {
        convertor = new DetailedResourceConverter(metadataService);
        convertor.setMapper(new DozerBeanMapper(Arrays.asList("mappings.xml")));
    }

    /**
     * Test of conversion from Resource to DetailedResourceVO
     * @throws Exception
     */
    @Test
    public void testConvertTo() throws Exception {
        initIdentity(resource);

        List<Capability> roots = Arrays.asList(initCustomCapabilityTree(resource, null));
        when(resource.getRootCapabilities()).thenReturn(roots);
        List<Property> props = Arrays.asList(initCustomProperty());
        when(resource.getProperties()).thenReturn(props);

        DetailedResourceVO vo = new DetailedResourceVO();
        convertor.convertTo(resource, vo);

        //test basic mapping
        assertEquals(CMP_NAME, vo.getIdentity().getName());
        assertEquals(version.toString(), vo.getIdentity().getVersion());
        assertEquals(size, vo.getIdentity().getSize());
        assertEquals(categories, vo.getIdentity().getCategories());

        //test detailed mapping

        //capabilities
        assertEquals(1, vo.getCapabilities().size());
        GenericCapabilityVO cap = vo.getCapabilities().get(0);
        testCapability(cap);

        //test child capability
        assertEquals(1, cap.getCapabilities().size()); //has child capability
        cap = cap.getCapabilities().get(0);
        testCapability(cap);

        //test resource requirements
        assertEquals(1, vo.getRequirements().size());
        testRequirement(vo.getRequirements().get(0));

        //test resource properties
        assertEquals(1, vo.getProperties().size());
        testProperty(vo.getProperties().get(0));
    }

    @Test
    @Ignore("Not implemented yet.")
    public void testConvertFrom() throws Exception {

    }

    /*
    DATA INITS AND HELPERS
     */

    private void testCapability(GenericCapabilityVO cap) {
        assertEquals(CUSTOM_ID, cap.getId());
        assertEquals(CUSTOM_CAPABILITY_NS, cap.getNamespace());

        //capability attribute
        assertEquals(1, cap.getAttributes().size());
        AttributeVO at = cap.getAttributes().get(0);
        testAttribute(at);

        //capability property
        assertEquals(1, cap.getProperties().size());
        PropertyVO prop = cap.getProperties().get(0);
        testProperty(prop);

        //property attribute
        assertEquals(1, prop.getAttributes().size());
        at = prop.getAttributes().get(0);
        testAttribute(at);

        //capability requirement
        assertEquals(1, cap.getRequirements().size());
        GenericRequirementVO req = cap.getRequirements().get(0);
        testRequirement(req);
    }

    private void testRequirement(GenericRequirementVO req) {
        assertEquals(CUSTOM_ID, req.getId());
        assertEquals(CUSTOM_REQUIREMENT_NS, req.getNamespace());
        assertEquals(1, req.getAttributes().size());
        testAttribute(req.getAttributes().get(0));
    }

    private void testAttribute(AttributeVO at) {
        assertEquals(CUSTOM_ATTR_NAME, at.getName());
        assertEquals(CUSTOM_ATTR_VALUE, at.getValue());
    }

    private void testProperty(PropertyVO prop) {
        assertEquals(CUSTOM_ID, prop.getId());
        assertEquals(CUSTOM_PROPERTY_NS, prop.getNamespace());
    }

    private void initIdentity(Resource resource) {
        Capability c = Mockito.mock(Capability.class);
        when(c.getNamespace()).thenReturn(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);

        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__NAME)).thenReturn(CMP_NAME);

        version = new Version(2,1,1);
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__VERSION)).thenReturn(version);

        size = 1024l;
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__SIZE)) .thenReturn(size);

        categories = new ArrayList<>();
        categories.add("osgi");
        categories.add("jar");
        categories.add("test");
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES)).thenReturn(categories);

        when(metadataService.getIdentity(resource)).thenReturn(c);
    }

    private Capability initCustomCapabilityTree(Resource resource, Capability root) {
        Capability c = Mockito.mock(Capability.class);
        when(c.getId()).thenReturn(CUSTOM_ID);
        when(c.getNamespace()).thenReturn(CUSTOM_CAPABILITY_NS);

        List list = Arrays.asList(initCustomAttribute());
        when(c.getAttributes()).thenReturn(list);
        list = Arrays.asList(initCustomProperty());
        when(c.getProperties()).thenReturn(list);
        list = Arrays.asList(initCustomRequirement(resource));
        when(c.getRequirements()).thenReturn(list);

        if (root == null) {
            list = Arrays.asList(initCustomCapabilityTree(resource, c));
            when(c.getChildren()).thenReturn(list);
        }

        return c;
    }

    private Property initCustomProperty() {
        Property p = Mockito.mock(Property.class);

        when(p.getId()).thenReturn(CUSTOM_ID);
        when(p.getNamespace()).thenReturn(CUSTOM_PROPERTY_NS);

        List list = Arrays.asList(initCustomAttribute());
        when(p.getAttributes()).thenReturn(list);

        return p;
    }

    private Attribute<?> initCustomAttribute() {
        Attribute a = Mockito.mock(Attribute.class);
        when(a.getName()).thenReturn(CUSTOM_ATTR_NAME);
        when(a.getValue()).thenReturn(CUSTOM_ATTR_VALUE);

        return a;
    }

    private Requirement initCustomRequirement(Resource resource) {
        Requirement r = Mockito.mock(Requirement.class);
        when(r.getNamespace()).thenReturn(CUSTOM_REQUIREMENT_NS);
        when(r.getId()).thenReturn(CUSTOM_ID);

        List list = Arrays.asList(initCustomAttribute());
        when(r.getAttributes()).thenReturn(list);

        if(resource != null) {
            when(resource.getRequirements()).thenReturn(Collections.singletonList(r));
        }

        return r;
    }
}