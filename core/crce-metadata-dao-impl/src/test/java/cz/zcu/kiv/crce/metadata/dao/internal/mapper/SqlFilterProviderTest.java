package cz.zcu.kiv.crce.metadata.dao.internal.mapper;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.dao.filter.CapabilityFilter;
import cz.zcu.kiv.crce.metadata.dao.filter.Operator;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceDAOFilter;

/**
 * Date: 14.3.16
 *
 * @author Jakub Danek
 */
public class SqlFilterProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(SqlFilterProvider.class);

    private SqlFilterProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new SqlFilterProvider();
    }

    /**
     * Desired SQL output for the test data and AND operator between capability filters:
     * <p>
     * SELECT DISTINCT r.resource_id, r.repository_id, r.id, r.uri FROM resource r
        JOIN capability c1 ON c1.RESOURCE_ID = r.RESOURCE_ID
        JOIN CAPABILITY_ATTRIBUTE ca11 ON ca11.CAPABILITY_ID = c1.CAPABILITY_ID
        JOIN CAPABILITY sc11 ON c1.CAPABILITY_ID = sc11.PARENT_CAPABILITY_ID
        JOIN CAPABILITY_ATTRIBUTE ca111 ON ca111.CAPABILITY_ID = sc11.CAPABILITY_ID
        JOIN CAPABILITY_ATTRIBUTE ca112 ON ca112.CAPABILITY_ID = sc11.CAPABILITY_ID
        LEFT JOIN capability c2 ON c2.RESOURCE_ID = r.RESOURCE_ID
        JOIN CAPABILITY_ATTRIBUTE ca21 ON ca21.CAPABILITY_ID = c2.CAPABILITY_ID
        WHERE (c1.NAMESPACE = 'crce.api.java.package' AND ca11.name = 'name' AND ca11.STRING_VALUE = 'cz.zcu.kiv.osgi.demo.parking.carpark.flow'
        AND (sc11.NAMESPACE = 'crce.api.java.class' AND ((ca111.name = 'name' AND ca111.STRING_VALUE = 'IVehicleFlow') AND (ca112.name = 'interface' AND ca112.BOOLEAN_VALUE = true)))
        )
        AND (c2.NAMESPACE = 'crce.api.java.package' AND ca21.name = 'name' AND ca21.STRING_VALUE = 'cz.zcu.kiv.osgi.demo.parking.carpark.gateway')
     </p>
     * @throws Exception
     */
    @Test
    public void testAndFilter() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(SqlFilterProvider.PARAM_FILTER, buildFilter(Operator.AND));

        String sql = provider.generateSQL(params);
        logger.info(sql);
    }

    private ResourceDAOFilter buildFilter(Operator operator) {
        ResourceDAOFilter filter = new ResourceDAOFilter();
        filter.setOperator(operator);

        List<Attribute<?>> attributes = new ArrayList<>();
        attributes.add(createAttribute("name", "IVehicleFlow"));
        attributes.add(createAttribute("interface", true));
        CapabilityFilter clazz = buildCapabilityFilter("crce.api.java.class", attributes, null);

        attributes = new ArrayList<>();
        attributes.add(createAttribute("name", "cz.zcu.kiv.osgi.demo.parking.carpark.flow"));
        CapabilityFilter pckg = buildCapabilityFilter("crce.api.java.package", attributes, Collections.singletonList(clazz));
        filter.addCapabilityFilter(pckg);

        attributes = new ArrayList<>();
        attributes.add(createAttribute("name", "cz.zcu.kiv.osgi.demo.parking.carpark.gateway"));
        pckg = buildCapabilityFilter("crce.api.java.package", attributes, null);
        filter.addCapabilityFilter(pckg);

        return filter;
    }

    private CapabilityFilter buildCapabilityFilter(String namespace, List<Attribute<?>> attributes, List<CapabilityFilter> subFilters) {
        CapabilityFilter f = new CapabilityFilter(namespace);
        if(attributes != null) {
            f.addAttributes(attributes);
        }
        if(subFilters != null) {
            f.addSubFilters(subFilters);
        }

        return f;
    }

    private Attribute<?> createAttribute(String name, Object value) {
        Attribute at = Mockito.mock(Attribute.class);
        when(at.getName()).thenReturn(name);
        when(at.getValue()).thenReturn(value);
        when(at.getStringValue()).thenReturn(value.toString());
        when(at.getType()).thenReturn(value.getClass());
        when(at.getOperator()).thenReturn(cz.zcu.kiv.crce.metadata.Operator.EQUAL);
        return at;
    }

}
