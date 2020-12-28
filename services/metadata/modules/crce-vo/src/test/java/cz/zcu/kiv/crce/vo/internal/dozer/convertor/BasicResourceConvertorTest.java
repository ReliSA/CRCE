package cz.zcu.kiv.crce.vo.internal.dozer.convertor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;

/**
 * Test for {@link BasicResourceConvertor} class.
 *
 * Date: 15.5.15
 *
 * @author Jakub Danek
 */
public class BasicResourceConvertorTest {

    private BasicResourceConvertor convertor;

    /*
        ##### MOCKS #####
     */
    @Mock
    private MetadataService metadataService;
    @Mock
    private Resource resource;

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
        convertor = new BasicResourceConvertor();
        convertor.setMetadataService(metadataService);
        convertor.setMapper(new DozerBeanMapper(Arrays.asList("mappings.xml")));
    }

    /**
     * Tests conversion from Resource to BasicResourceVO.
     *
     * @throws Exception
     */
    @Test
    public void testConvertTo() throws Exception {
        Capability c = Mockito.mock(Capability.class);
        when(c.getNamespace()).thenReturn(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);

        String name = "cz.zcu.kiv.vo.test.Component";
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__NAME)).thenReturn(name);

        Version version = new Version(2,1,1);
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__VERSION)).thenReturn(version);

        Long size = 1024l;
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__SIZE)) .thenReturn(size);

        List<String> categories = new ArrayList<>();
        categories.add("osgi");
        categories.add("jar");
        categories.add("test");
        when(c.getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES)).thenReturn(categories);

        when(metadataService.getIdentity(resource)).thenReturn(c);

        BasicResourceVO vo = convertor.convertTo(resource, null);
        assertEquals(name, vo.getIdentity().getName());
        assertEquals(version.toString(), vo.getIdentity().getVersion());
        assertEquals(size, vo.getIdentity().getSize());
        assertEquals(categories, vo.getIdentity().getCategories());
    }
}