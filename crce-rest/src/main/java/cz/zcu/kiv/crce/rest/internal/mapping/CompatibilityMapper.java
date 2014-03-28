package cz.zcu.kiv.crce.rest.internal.mapping;

import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.namespace.NsCrceCompatibility;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.ObjectFactory;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Property;

/**
 * Java-to-JAXB mapping class for Compatibility data.
 * <p/>
 * Date: 18.3.14
 *
 * @author Jakub Danek
 */
public class CompatibilityMapper {

    private ObjectFactory compatibilityObjectFactory = new ObjectFactory();
    private cz.zcu.kiv.crce.rest.internal.jaxb.metadata.ObjectFactory metadataObjectFactory = new cz.zcu.kiv.crce.rest.internal.jaxb.metadata.ObjectFactory();

    /**
     * Map compatibility data related to the resource into a Property and attach the property
     * to the resource.
     *
     * @param resource resource to map
     * @return Property wrapper around the compatibility data
     */
    @Nullable
    public Property mapCompatibility(Resource resource) {
        List<Compatibility> compatibilities = Activator.instance().getCompatibilityService().listLowerCompatibilities(resource);

        Property compProp = null;

        if (!compatibilities.isEmpty()) {
            compProp = metadataObjectFactory.createProperty();
            compProp.setNamespace(NsCrceCompatibility.NAMESPACE__CRCE_COMPATIBILITY);

            Element e;
            for (Compatibility c : compatibilities) {
                e = marshallToElement(mapCompatibility(c));

                if (e != null) {
                    compProp.getAnies().add(e);
                }
            }

        }


        return compProp;
    }

    /**
     * Marshall JAXB Compatibility class into DOM Element.
     *
     * @param o JAXB compatibility instance
     * @return DOM Element
     */
    private Element marshallToElement(cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Compatibility o) {
        try {
            Class<?> clazz = o.getClass();
            ClassLoader cl = ObjectFactory.class.getClassLoader();

            JAXBContext jc = JAXBContext.newInstance(clazz.getPackage().getName(), cl);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            DOMResult res = new DOMResult();
            m.marshal(o, res);

            return ((Document) res.getNode()).getDocumentElement();
        } catch (JAXBException e) {
            return null;
        }
    }


    /**
     * Map CRCE Compatibility instance to JAXB
     *
     * @param compatibility
     * @return
     */
    private cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Compatibility mapCompatibility(Compatibility compatibility) {
        cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Compatibility jaxbCompatibility = compatibilityObjectFactory.createCompatibility();

        //different vendor
        if (!compatibility.getResourceName().equals(compatibility.getBaseResourceName())) {
            jaxbCompatibility.setBaseName(compatibility.getBaseResourceName());
        }
        jaxbCompatibility.setBaseVersion(compatibility.getBaseResourceVersion().toString());
        jaxbCompatibility.setContract("syntax"); //TODO incorporate into Compatibility data
        jaxbCompatibility.setValue(compatibility.getDiffValue().toString());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Diff> jaxbDiffs = jaxbCompatibility.getDifves();
        for (Diff d : compatibility.getDiffDetails()) {
            jaxbDiffs.add(mapDiff(d));
        }

        return jaxbCompatibility;
    }

    /**
     * Map CRCE Diff instance to JAXB
     *
     * @param diff
     * @return
     */
    private cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Diff mapDiff(Diff diff) {
        cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Diff jaxbDiff = compatibilityObjectFactory.createDiff();

        jaxbDiff.setName(diff.getName());
        jaxbDiff.setNamespace(diff.getNamespace());
        //display only on package level
        if (diff.getLevel() == DifferenceLevel.PACKAGE) {
            jaxbDiff.setSyntax("java"); //TODO make part of Diff data
        }
        jaxbDiff.setValue(diff.getValue().toString());
        jaxbDiff.setLevel(diff.getLevel().toString());
        if (diff.getRole() != null) {
            jaxbDiff.setRole(diff.getRole().toString());
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.compatibility.Diff> children = jaxbDiff.getDifves();
        for (Diff d : diff.getChildren()) {
            children.add(mapDiff(d));
        }

        return jaxbDiff;
    }

}
