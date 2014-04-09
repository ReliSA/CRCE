package cz.zcu.kiv.crce.compatibility.internal.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cz.zcu.kiv.jacc.javatypes.HasName;
import cz.zcu.kiv.obcc.bundletypes.JOSGiBundle;
import cz.zcu.kiv.typescmp.CmpResult;
import cz.zcu.kiv.typescmp.CmpResultInfo;
import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;
import cz.zcu.kiv.crce.compatibility.namespace.NsCrceCompatibility;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiService;

/**
 * PARSING OF THE CmpResult structure.
 * <p/>
 * Unfortunately, the whole process lacks documentation on the OBCC and JaCC side.
 * The following design should however fit most, if not all, scenarios that might occur.
 * <p/>
 * All unsupported scenarios are skipped and allow the process to continue.
 * <p/>
 * List<Diff> parseRootCmpResultInfo(CmpResultInfo info) parses whole diff section based on namespace
 * and role (e.g. osgi.wiring.package and CAPABILITY - that is differences among exported
 * packages).
 * <p/>
 * <p/>
 * List<Diff> parseCmpResultInfoList(List<CmpResultInfo> infos, DifferenceRole role, String namespace)
 * is a convenience method for parsing whole list of infos. Only iterates over the list and
 * calls parsing method.
 * <p/>
 * <p/>
 * List<Diff> parseContainerCmpResultInfo(CmpResultInfo i, DifferenceRole role, String namespace)
 * is used to traverse through container CmpResults (e.g. list containing all method changes).
 * If i is not a container CmpResult, cascades directly into final parsing method.
 * <p/>
 * <p/>
 * Diff parseCmpResultInfo(CmpResultInfo i, DifferenceRole role, String namespace)
 * Extracts or required pieces of information from the result (name, diff value) and
 * starts parsing of the CmpResult children.
 * <p/>
 * <p/>
 * Date: 15.3.14
 *
 * @author Jakub Danek
 */
class CmpResultParser {

    private CompatibilityFactory compatibilityFactory;

    CmpResultParser(CompatibilityFactory factory) {
        this.compatibilityFactory = factory;
    }

    @Nonnull
    public List<Diff> extractDiffDetails(@Nullable CmpResult<JOSGiBundle> res) {
        List<Diff> topDiffs = new ArrayList<>();

        if (res != null) {
            List<Diff> tmp;
            for (CmpResultInfo info : res.getChildren()) {
                tmp = parseRootCmpResultInfo(info);
                if (tmp != null) {
                    topDiffs.addAll(tmp);
                }
            }
        }

        return topDiffs;
    }

    /**
     * Parses first level of returned CmpResult object. Namespace and role are retrieved
     * from the top container.
     *
     * @param info
     * @return
     */
    private List<Diff> parseRootCmpResultInfo(CmpResultInfo info) {
        DifferenceRole role;
        String namespace;
        switch (info.getContentCode()) {
            case "cmp.child.osgi.exported.packages":
                role = DifferenceRole.CAPABILITY;
                namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;
                break;
            case "cmp.child.osgi.imported.packages":
                role = DifferenceRole.REQUIREMENT;
                namespace = NsOsgiPackage.NAMESPACE__OSGI_PACKAGE;
                break;
            case "cmp.child.osgi.exported.services":
                role = DifferenceRole.CAPABILITY;
                namespace = NsOsgiService.NAMESPACE__OSGI_SERVICE;
                break;
            case "cmp.child.osgi.imported.services":
                role = DifferenceRole.REQUIREMENT;
                namespace = NsOsgiService.NAMESPACE__OSGI_SERVICE;
                break;

            default:
                return null;
        }

        return parseCmpResultInfoList(info.getResult().getChildren(), role, namespace);
    }

    /**
     * Helper method for parsing list of results.
     *
     * @param infos     list of CmpResultInfo to be parsed
     * @param role      parent role
     * @param namespace parent namespace
     * @return
     */
    private List<Diff> parseCmpResultInfoList(List<CmpResultInfo> infos, DifferenceRole role, String namespace) {
        List<Diff> diffs = new ArrayList<>(infos.size());

        List<Diff> tmp;
        for (CmpResultInfo i : infos) {
            tmp = parseContainerCmpResultInfo(i, role, namespace);
            if (tmp != null) {
                diffs.addAll(tmp);
            }
        }

        return diffs;
    }

    /**
     * Parse container CmpResultInfo objects or delegate by the chain to regular parsing method.
     *
     * @param i         CmpResultInfo to be parsed
     * @param role      parent role
     * @param namespace parent namespace
     * @return
     */
    private List<Diff> parseContainerCmpResultInfo(CmpResultInfo i, DifferenceRole role, String namespace) {
        CmpResult<?> res = i.getResult();
        if (res.getDiff() == Difference.NON) {
            return null;
        }

        List<Diff> ret = new ArrayList<>();

        Diff tmp;
        switch (i.getContentCode()) {
            case "cmp.child.osgi.package.type":

                if (res.getChildren() != null && !res.getChildren().isEmpty()) {
                    ret.addAll(parseCmpResultInfoList(res.getChildren(), role, namespace));
                } else {
                    //hierarchy ends here, just parse the OSGi package
                    tmp = parseCmpResultInfo(i, role, namespace);
                    if (tmp != null) {
                        ret.add(tmp);
                    }
                }

                break;
            case "cmp.child.methods":
            case "cmp.child.fields":
            case "cmp.child.constructors":
                ret.addAll(parseCmpResultInfoList(res.getChildren(), role, namespace));
                break;
            default:

                tmp = parseCmpResultInfo(i, role, namespace);
                if (tmp != null) {
                    ret.add(tmp);
                }
                break;
        }

        return ret;
    }

    /**
     * Parse CmpResultInfo into a Diff instance.
     *
     * @param i         to be parsed
     * @param role      parent role
     * @param namespace parent namespace
     * @return
     */
    private Diff parseCmpResultInfo(CmpResultInfo i, DifferenceRole role, String namespace) {
        CmpResult<?> res = i.getResult();
        if (res.getDiff() == Difference.NON) {
            return null;
        }

        Diff d = compatibilityFactory.createEmptyDiff();

        HasName p;
        try { //ignore unknown objects

            p = (HasName) (res.getFirstObject() != null ? res.getFirstObject() : res.getSecondObject());
            if (p == null) {
                return null;
            }

        } catch (ClassCastException ex) {
            return null;
        }

        switch (i.getContentCode()) {
            case "cmp.child.package":
            case "cmp.child.osgi.package.type": //in cases where the whole package is added in revision
                d.setNamespace(namespace);
                d.setRole(role);
                d.setLevel(DifferenceLevel.PACKAGE);
                d.setSyntax(NsCrceCompatibility.DIFF_SYNTAX_JAVA);
                break;
            case "cmp.child.package.class":
                d.setLevel(DifferenceLevel.TYPE);
                break;
            case "cmp.child.method.type":
            case "cmp.child.constructor.type":
                d.setLevel(DifferenceLevel.OPERATION);
                break;
            case "cmp.child.field.type":
                d.setLevel(DifferenceLevel.FIELD);
                break;
            default:
                return null;

        }

        d.setName(p.getName());
        d.setValue(res.getDiff());
        d.addChildren(parseCmpResultInfoList(res.getChildren(), role, namespace));

        return d;
    }
}
