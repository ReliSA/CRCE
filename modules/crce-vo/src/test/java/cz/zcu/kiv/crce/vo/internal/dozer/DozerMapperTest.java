package cz.zcu.kiv.crce.vo.internal.dozer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;
import cz.zcu.kiv.crce.compatibility.namespace.NsCrceCompatibility;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.vo.model.compatibility.CompatibilityVO;
import cz.zcu.kiv.crce.vo.model.compatibility.DiffVO;

/**
 * Date: 4.9.15
 *
 * @author Jakub Danek
 */
public class DozerMapperTest {


    private static Mapper mapper;

    @BeforeClass
    public static void setUpClass() throws Exception {
        List<String> mappings = new LinkedList<>();
        mappings.add("mappings.xml");
        mapper = new DozerBeanMapper(mappings);
    }

    @Test
    public void testCompatibilityTo() throws Exception {
        Compatibility mock = initCompatibilityMock();
        CompatibilityVO vo = mapper.map(mock, CompatibilityVO.class);

        assertEquals(NsCrceCompatibility.NAMESPACE__CRCE_COMPATIBILITY, vo.getNamespace());
        assertEquals(mock.getId(), vo.getId());
        assertEquals(mock.getResourceName(), vo.getOtherExternalId());
        assertEquals(mock.getBaseResourceName(), vo.getBaseExternalId());
        assertEquals(mock.getResourceVersion(), new Version(vo.getOtherVersion()));
        assertEquals(mock.getBaseResourceVersion(), new Version(vo.getBaseVersion()));
        assertEquals(mock.getContract(), vo.getContract());
        assertEquals(mock.getDiffValue(), vo.getDiffValue());

        int i = 0;
        //noinspection ConstantConditions
        for (Diff diff : mock.getDiffDetails()) {
            DiffVO dvo = vo.getDiffs().get(i);
            compareDiffs(diff, dvo);
            i++;
        }

    }

    @Test
    @Ignore("not implemented yet")
    public void testCompatibilityFrom() throws Exception {


    }

    private void compareDiffs(Diff diff, DiffVO dvo) {
        assertEquals(diff.getLevel(), dvo.getLevel());
        assertEquals(diff.getName(), dvo.getName());
        assertEquals(diff.getRole(), dvo.getRole());
        assertEquals(diff.getSyntax(), dvo.getSyntax());
        assertEquals(diff.getValue(), dvo.getValue());
        assertEquals(diff.getNamespace(), dvo.getNamespace());

        int i = 0;
        for (Diff d : diff.getChildren()) {
            DiffVO dv = dvo.getChildren().get(i);
            compareDiffs(d, dv);
            i++;
        }
    }

    private Compatibility initCompatibilityMock() {
        Compatibility cmp = Mockito.mock(Compatibility.class);

        when(cmp.getId()).thenReturn("UUID-UUID-UUID");
        when(cmp.getBaseResourceName()).thenReturn("test.component.bundle");
        when(cmp.getBaseResourceVersion()).thenReturn(new Version(1,0,4, "qualifier"));
        when(cmp.getDiffValue()).thenReturn(Difference.DEL);
        when(cmp.getContract()).thenReturn(Contract.SYNTAX);
        when(cmp.getResourceName()).thenReturn("test.component.bundle");
        when(cmp.getResourceVersion()).thenReturn(new Version(2,0,1,"ololo"));

        List<Diff> diffs = new LinkedList<>();
        diffs.add(initDiff("cz.kiv.pokus.a", DifferenceLevel.PACKAGE, Difference.DEL, true));
        diffs.add(initDiff("cz.kiv.pokus.b", DifferenceLevel.PACKAGE, Difference.NON, true));
        when(cmp.getDiffDetails()).thenReturn(diffs);

        return cmp;
    }

    private Diff initDiff(String name, DifferenceLevel level, Difference val, boolean includeChildren) {
        Diff diff = Mockito.mock(Diff.class);

        when(diff.getName()).thenReturn(name);
        when(diff.getValue()).thenReturn(val);
        when(diff.getLevel()).thenReturn(level);
        when(diff.getRole()).thenReturn(DifferenceRole.CAPABILITY);
        when(diff.getSyntax()).thenReturn(NsCrceCompatibility.DIFF_SYNTAX_JAVA);

        List<Diff> diffs = new LinkedList<>();
        if(includeChildren) {
            diffs.add(initDiff("Foo", DifferenceLevel.TYPE, val, false));
            diffs.add(initDiff("Bar", DifferenceLevel.TYPE, Difference.NON, false));

        }
        when(diff.getChildren()).thenReturn(diffs);

        return diff;
    }
}
