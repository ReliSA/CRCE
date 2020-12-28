package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.vo.model.metadata.AttributeVO;
import cz.zcu.kiv.crce.vo.model.metadata.DirectiveVO;
import cz.zcu.kiv.crce.vo.model.metadata.GenericRequirementVO;

/**
 * Date: 29.4.16
 *
 * @author Jakub Danek
 */
public class RequirementConvertorTest {

    private RequirementConvertor convertor;

    /*
        ##### MOCKS #####
     */
    @Mock
    private MetadataFactory metadataFactory;

    @Before
    public void setUp() throws Exception {
        initMocks();
        initDozer();
    }

    private void initMocks() {
        MockitoAnnotations.initMocks(this);
        reset(metadataFactory);
    }


    private void initDozer() {
        convertor = new RequirementConvertor(metadataFactory);
        convertor.setMapper(new DozerBeanMapper(Arrays.asList("mappings.xml")));
    }

    /**
     * Test of conversion from Requirement to GenericRequirementVO
     * @throws Exception
     */
    @Test
    public void testConvertTo() throws Exception {
        Requirement r = initTestRequirement();

        GenericRequirementVO vo = convertor.convertTo(r);

        checkReq(r, vo);

        assertEquals(vo.getChildren().size(), r.getChildren().size());
        for (GenericRequirementVO v : vo.getChildren()) {
            for (Requirement requirement : r.getChildren()) {
                checkReq(requirement, v);
            }
        }
    }

    /**
     * Test of conversion from GenericRequirementVO  to  Requirement
     * @throws Exception
     */
    @Test
    public void testConvertFrom() throws Exception {
        when(metadataFactory.createAttribute(Mockito.any(AttributeType.class), Mockito.any())).then(new Answer<Attribute>() {
            @Override
            public Attribute answer(InvocationOnMock invocation) throws Throwable {
                Attribute at = Mockito.mock(Attribute.class);
                when(at.getAttributeType()).thenReturn((AttributeType) invocation.getArguments()[0]);
                when(at.getValue()).thenReturn(invocation.getArguments()[1]);
                return at;
            }
        });

        when(metadataFactory.createRequirement(Mockito.any(String.class), Mockito.any(String.class))).then(new Answer<Requirement>() {
            @Override
            public Requirement answer(InvocationOnMock invocation) throws Throwable {
                return new RequirementImpl((String) invocation.getArguments()[0], (String) invocation.getArguments()[1]);
            }
        });

        GenericRequirementVO vo = initTestRequirementVO();
        Requirement r = convertor.convertFrom(vo);

        checkReq(r, vo);
    }

    private void checkReq(Requirement r, GenericRequirementVO vo) {
        assertEquals(r.getNamespace(), vo.getNamespace());
        assertEquals(r.getId(), vo.getId());

        checkAttributes(r.getAttributes(), vo.getAttributes());
        checkDirectives(r.getDirectives(), vo.getDirectives());
    }

    private void checkDirectives(Map<String, String> dirs, List<DirectiveVO> vos) {
        for (DirectiveVO vo : vos) {
            assertTrue(dirs.containsKey(vo.getName()));
            assertEquals(vo.getValue(), dirs.get(vo.getName()));
        }

        assertEquals(vos.size(), dirs.size());
    }

    private void checkAttributes(List<Attribute<?>> ats, List<AttributeVO> vos) {
        for (Attribute<?> at : ats) {
            for (AttributeVO vo : vos) {
                if(Objects.equals(vo.getName(), at.getName())) {
                    assertEquals(at.getAttributeType().getType().getName(), vo.getType());
                    assertEquals(at.getValue().toString(), vo.getValue());
                }
            }
        }

        assertEquals(ats.size(), vos.size());
    }


    private Requirement initTestRequirement() {
        Requirement r = Mockito.mock(Requirement.class);

        when(r.getNamespace()).thenReturn(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        when(r.getId()).thenReturn("UUID123456789");

        List<Attribute<?>> ats = initAttributes();
        when(r.getAttributes()).thenReturn(ats);
        when(r.getDirectives()).thenReturn(initDirectives());

        List<Requirement> reqs = initChildReqs();
        when(r.getChildren()).thenReturn(reqs);

        return r;
    }

    private List<Requirement> initChildReqs() {
        List<Requirement> reqs = new LinkedList<>();
        Requirement r = Mockito.mock(Requirement.class);

        when(r.getNamespace()).thenReturn(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        when(r.getId()).thenReturn("987654321UUID");

        List<Attribute<?>>  ats = initAttributes();
        when(r.getAttributes()).thenReturn(ats);
        when(r.getDirectives()).thenReturn(initDirectives());

        return reqs;
    }

    private Map<String, String> initDirectives() {
        Map<String, String> dirs = new HashMap<>();
        dirs.put("operator", "AND");
        dirs.put("directive", "test");
        return  dirs;
    }


    private List<Attribute<?>> initAttributes() {
        List<Attribute<?>> attributes = new LinkedList<>();

        Attribute a = Mockito.mock(Attribute.class);
        when(a.getName()).thenReturn(NsCrceIdentity.ATTRIBUTE__NAME.getName());
        when(a.getAttributeType()).thenReturn(NsCrceIdentity.ATTRIBUTE__NAME);
        when(a.getValue()).thenReturn("requirementName");
        attributes.add(a);

        a = Mockito.mock(Attribute.class);
        when(a.getName()).thenReturn(NsCrceIdentity.ATTRIBUTE__VERSION.getName());
        when(a.getAttributeType()).thenReturn(NsCrceIdentity.ATTRIBUTE__VERSION);
        when(a.getValue()).thenReturn(new Version(1,1,0));
        attributes.add(a);


        return attributes;
    }

    private GenericRequirementVO initTestRequirementVO() {
        GenericRequirementVO r = new GenericRequirementVO();

        r.setNamespace(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        r.setId("UUID123456789");

        r.setAttributes(initAttributeVOs());
        r.setDirectives(initDirectiveVOs());
        r.setChildren(initChildReqVOs());

        return r;
    }

    private List<GenericRequirementVO> initChildReqVOs() {
        List<GenericRequirementVO> reqs = new LinkedList<>();
        GenericRequirementVO r = new GenericRequirementVO();

        r.setNamespace(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        r.setId("987654321UUID");

        r.setAttributes(initAttributeVOs());
        r.setDirectives(initDirectiveVOs());

        return reqs;
    }

    private List<DirectiveVO> initDirectiveVOs() {
        List<DirectiveVO> dirs = new LinkedList<>();
        dirs.add(new DirectiveVO("operator", "AND"));
        dirs.add(new DirectiveVO("directive", "test"));
        return  dirs;
    }


    private List<AttributeVO> initAttributeVOs() {
        List<AttributeVO> attributes = new LinkedList<>();

        AttributeVO a = new AttributeVO(NsCrceIdentity.ATTRIBUTE__NAME.getName(), "requirementName", NsCrceIdentity.ATTRIBUTE__NAME.getType());
        attributes.add(a);

        a = new AttributeVO(NsCrceIdentity.ATTRIBUTE__VERSION.getName(), "1.1.0", NsCrceIdentity.ATTRIBUTE__VERSION.getType());
        attributes.add(a);


        return attributes;
    }

}
